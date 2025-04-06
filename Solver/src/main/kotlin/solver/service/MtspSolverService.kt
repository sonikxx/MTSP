package solver.service

import io.grpc.Status
import io.grpc.StatusException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.dto.*
import solver.repository.MtspSolutionRepository
import java.time.Instant

@Service
class MtspSolverService(
    private val tspAlgorithm: TspAlgorithm,
    private val mtspSolutionRepository: MtspSolutionRepository,
) {

    suspend fun solve(request: MtspSolverRequest) {
        logger.info { "${request.requestId}: gRPC Request received for MTSP solving!" }

        val namesToId = mutableMapOf<String, Int>()
        request.cities.forEach { city ->
            namesToId[city.name] = namesToId.size
        }
        if (namesToId.size != request.cities.size) {
            logger.error { "${request.requestId}: gRPC Request contains duplicate city names!" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Duplicate city names are not allowed"))
        }

        if (request.numSalesmen <= 0 || request.numSalesmen > request.cities.size) {
            logger.error { "${request.requestId}: Invalid number of salesmen: ${request.numSalesmen}" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Number of salesmen must be positive and less than the number of cities"))
        }

        val cities = request.cities.mapIndexed { index, city ->
            Point(index, city.x, city.y)
        }
        val numSalesmen = request.numSalesmen


        val startTime = Instant.now()


        tspAlgorithm.solveMtsp(cities, numSalesmen)
            .collect { (status, solution) ->
                logger.info { "best = ${solution.totalDistance} for: ${solution.cities}" }

                val currentSolution = MtspSolution(
                    userId = request.userId,
                    requestId = request.requestId,
                    totalCost = solution.totalDistance,
                    completedAt = Instant.now(),
                    createdAt = startTime,
                    status = status,
                )
                solution.cities.mapIndexed { index, route ->
                    currentSolution.addRoute(
                        MtspRoute(
                            currentSolution,
                            index,
                            route.map { point ->
                                request.cities[point.id].name
                            }
                        )
                    )
                }

                mtspSolutionRepository.save(currentSolution)
                Thread.sleep(100)
            }
        logger.info { "Solving for `${request.requestId}` is completed!" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
