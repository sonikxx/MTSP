package solver

import com.example.grpc.*
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.springframework.grpc.server.service.GrpcService
import solver.dto.Point
import java.lang.Thread.sleep

@GrpcService
class MtspSolverService(private val tspAlgorithm: TspAlgorithm) : MtspSolverGrpcKt.MtspSolverCoroutineImplBase() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun solve(request: MtspSolverRequest): Flow<MtspSolverResponse> = flow {
        logger.info { "${request.requestId}: gRPC Request received for MTSP solving!" }

        val namesToId = mutableMapOf<String, Int>()
        request.citiesList.forEach { city ->
            namesToId[city.name] = namesToId.size
        }
        if (namesToId.size != request.citiesList.size) {
            logger.error { "${request.requestId}: gRPC Request contains duplicate city names!" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Duplicate city names are not allowed"))
        }

        if (request.numSalesmen <= 0) {
            logger.error { "${request.requestId}: Invalid number of salesmen: ${request.numSalesmen}" }
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Number of salesmen must be positive"))
        }

        val cities = request.citiesList.mapIndexed { index, city ->
            Point(index, city.x, city.y)
        }
        val numSalesmen = request.numSalesmen

        Thread.sleep(5000)

        tspAlgorithm.solveMtsp(cities, numSalesmen)
            .collect { solution ->
                logger.info { "${request.requestId}: gRPC Response sent for MTSP solving!" }
                Thread.sleep(2000)
                val response = MtspSolverResponse.newBuilder().apply {
                    solution.forEach { (salesmanId, route) ->
                        addRoutes(
                            SalesmanRoute.newBuilder().apply {
                                this.salesmanId = salesmanId
                                route.forEach { city ->
                                    addRoute(
                                        City.newBuilder()
                                            .setX(city.x)
                                            .setY(city.y)
                                            .setName(request.citiesList[city.id].name)
                                            .build()
                                    )
                                }
                            }.build()
                        )
                    }
                }.build()
                emit(response)
            }
        logger.info { "${request.requestId}: gRPC Streaming completed!" }
    }
}
