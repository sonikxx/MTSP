package solver

import io.grpc.Status
import io.grpc.StatusException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.dto.MtspSolverRequest
import solver.dto.Point
import java.lang.Thread.sleep

@Service
class MtspSolverService(private val tspAlgorithm: TspAlgorithm) {

    suspend fun solve(request: MtspSolverRequest) {
        logger.info { "${request.requestId}: gRPC Request received for MTSP solving!" }

        val namesToId = mutableMapOf<String, Int>()
        request.cities .forEach { city ->
            namesToId[city.name] = namesToId.size
        }
        if (namesToId.size != request.cities.size) {
            logger.error { "${request.requestId}: gRPC Request contains duplicate city names!" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Duplicate city names are not allowed"))
        }

        if (request.numSalesmen <= 0) {
            logger.error { "${request.requestId}: Invalid number of salesmen: ${request.numSalesmen}" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Number of salesmen must be positive"))
        }

        val cities = request.cities.mapIndexed { index, city ->
            Point(index, city.x, city.y)
        }
        val numSalesmen = request.numSalesmen

//        Thread.sleep(5000)
//        tspAlgorithm.solveMtsp(cities, numSalesmen)

        tspAlgorithm.solveMtsp(cities, numSalesmen)
            .collect { solution ->
                logger.info { "${request.requestId}: gRPC Response sent for MTSP solving!" }
                logger.info { "best = ${solution.totalDistance} for: ${solution.cities}" }
                Thread.sleep(1000)
//                val response = MtspSolverResponse.newBuilder().apply {
//                    solution.forEach { (salesmanId, route) ->
//                        addRoutes(
//                            SalesmanRoute.newBuilder().apply {
//                                this.salesmanId = salesmanId
//                                route.forEach { city ->
//                                    addRoute(
//                                        City.newBuilder()
//                                            .setX(city.x)
//                                            .setY(city.y)
//                                            .setName(request.citiesList[city.id].name)
//                                            .build()
//                                    )
//                                }
//                            }.build()
//                        )
//                    }
//                }.build()
//                emit(response)
                // TODO: db insert
            }
        logger.info { "${request.requestId}: gRPC Streaming completed!" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
