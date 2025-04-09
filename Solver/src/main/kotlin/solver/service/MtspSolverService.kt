package solver.service

import io.grpc.Status
import io.grpc.StatusException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.algorithm.MtspAlgorithmFactory
import solver.dto.*
import solver.repository.MtspSolutionRepository
import java.time.Instant

@Service
class MtspSolverService(
    private val mtspAlgorithmFactory: MtspAlgorithmFactory,
    private val mtspSolutionRepository: MtspSolutionRepository,
) {

    suspend fun solve(request: MtspRequest) {
        val namesToId = mutableMapOf<City, Int>()
        request.cities.forEach { city ->
            namesToId[city] = namesToId.size
        }
        if (namesToId.size != request.cities.size) {
            logger.error { "${request.id}: Request contains duplicate city names!" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Duplicate city names are not allowed"))
        }

        if (request.salesmanNumber <= 0 || request.salesmanNumber > request.cities.size) {
            logger.error { "${request.id}: Invalid number of salesmen: ${request.salesmanNumber}" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Number of salesmen must be positive and less than the number of cities"))
        }

        val points = request.cities.map{ city ->
            Point(namesToId[city]!!)
        }.shuffled()

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
        for (edge in request.edges) {
            distances[edge.fromNode][edge.toNode] = edge.distance
        }

        tspAlgorithm.solve(points, distances, request.salesmanNumber)
            .collect { (status, solution) ->
                logger.info { "best = ${solution.totalDistance} for: ${solution.cities}" }

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
                            route.map { point -> request.cities[point.id] }
                        )
                    )
                }

                mtspSolutionRepository.save(currentSolution)
                Thread.sleep(100)
            }
        logger.info { "Solving for `${request.id}` is completed!" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
