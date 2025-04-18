package solver.service

import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.algorithm.MtspAlgorithmFactory
import solver.dto.MtspSolution
import solver.dto.City
import solver.dto.MtspRequest
import solver.dto.Point
import solver.dto.RequestStatus
import solver.dto.SolutionStatus
import solver.dto.MtspRoute
import solver.repository.MtspRequestRepository
import solver.repository.MtspSolutionRepository
import java.time.Instant

@Service
class MtspSolverService(
    private val mtspAlgorithmFactory: MtspAlgorithmFactory,
    private val mtspRequestRepository: MtspRequestRepository,
    private val mtspSolutionRepository: MtspSolutionRepository,
) {

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun solve(request: MtspRequest) {
        val namesToId = mutableMapOf<City, Int>()
        request.map.cities.forEach { city ->
            namesToId[city] = namesToId.size
        }
        if (namesToId.size != request.map.cities.size) {
            logger.error { "${request.id}: Request contains duplicate city names!" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Duplicate city names are not allowed"))
        }

        if (request.salesmanNumber <= 0 || request.salesmanNumber > request.map.cities.size) {
            logger.error { "${request.id}: Invalid number of salesmen: ${request.salesmanNumber}" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Number of salesmen must be positive and less than the number of cities"))
        }

        val points = request.map.cities.map{ city ->
            Point(namesToId[city]!!)
        }

        val startTime = Instant.now()

        val tspAlgorithm = mtspAlgorithmFactory.get(request.algorithm)
        if (tspAlgorithm == null) {
            logger.error { "${request.id}: Unknown algorithm: ${request.algorithm}" }
            mtspSolutionRepository.save(
                MtspSolution(
                    userId = request.userId,
                    requestId = request.id,
                    totalCost = Double.MAX_VALUE,
                    completedAt = Instant.now(),
                    status = SolutionStatus.FAILED
                )
            )
            return
        }

        val nodeCount = namesToId.size
        val distances = Array(nodeCount) { Array(nodeCount) { Double.POSITIVE_INFINITY } }

        // Populate with actual distances
        for (edge in request.map.edges) {
            distances[edge.fromNode][edge.toNode] = edge.distance
        }

        val scope = CoroutineScope(Dispatchers.Default)
        val solvingJob = scope.launch {
            tspAlgorithm.solve(points, distances, request.salesmanNumber)
                .collect { (status, solution) ->
                    logger.info { "reqId = ${request.id} | best = ${solution.totalDistance} for size ${request.map.cities.size}" }

                    val currentSolution = MtspSolution(
                        userId = request.userId,
                        requestId = request.id,
                        totalCost = solution.totalDistance,
                        createdAt = startTime,
                        completedAt = Instant.now(),
                        status = status,
                    )
                    solution.cities.mapIndexed { index, route ->
                        currentSolution.addRoute(
                            MtspRoute(
                                currentSolution,
                                index,
                                route.map { point -> request.map.cities[point.id] }
                            )
                        )
                    }

                    // Non blocking save
                    withContext(Dispatchers.IO) {
                        mtspSolutionRepository.save(currentSolution)
                    }

                    delay(300)
                }
        }
        val cancelJob = scope.launch {
            val tickerChannel = ticker(5000, 0)

            for (event in tickerChannel) {
                val requestStatus = mtspRequestRepository.findStatusById(request.id)?: RequestStatus.CANCELED
                if (requestStatus == RequestStatus.CANCELED) {
                    logger.info { "Task has been canceled, stopping solving." }
                    solvingJob.cancel()
                    tickerChannel.cancel()
                    break
                }
            }
        }

        // Wait for solvingJob to complete
        solvingJob.join()
        // Cancel the cancelJob))
        cancelJob.cancel()
        cancelJob.join()

        withContext(Dispatchers.IO) {
            if (mtspRequestRepository.findStatusById(request.id) != RequestStatus.CANCELED) {
                mtspRequestRepository.updateStatusById(request.id, RequestStatus.SOLVED)
            }
        }

        logger.info { "Solving for `${request.id}` is completed!" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
