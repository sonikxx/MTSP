package solver.service

import com.example.grpc.MtspSolverRequest
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
        val namesToId = mutableMapOf<String, Int>()
        request.points.forEach { point ->
            namesToId[point] = namesToId.size
        }
        if (namesToId.size != request.points.size) {
            logger.error { "${request.id}: Request contains duplicate city names!" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Duplicate city names are not allowed"))
        }

        if (request.salesmanNumber <= 0 || request.salesmanNumber > request.points.size) {
            logger.error { "${request.id}: Invalid number of salesmen: ${request.salesmanNumber}" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Number of salesmen must be positive and less than the number of cities"))
        }

        val cities = request.points.map{ point ->
            Point(namesToId[point]!!)
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

        tspAlgorithm.solve(cities, distances, request.salesmanNumber)
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
                            route.map { point ->
                                request.points[point.id]
                            }
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
