package api

import api.config.BackendConfig
import api.dto.MtspRequestDto
import com.example.grpc.City
import com.example.grpc.MtspSolverRequest
import org.springframework.stereotype.Service
import io.grpc.ManagedChannelBuilder
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.util.*


@Service
class GrpcClientService(
    backendConfig: BackendConfig,
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val grpcChannel = ManagedChannelBuilder.forAddress(backendConfig.mtsp.host, backendConfig.mtsp.port)
        .usePlaintext()  // Отключаем шифрование (для теста можно использовать)
        .build()

    private val stub: MtspSolverGrpcKt.MtspSolverCoroutineStub =
        MtspSolverGrpcKt.MtspSolverCoroutineStub(grpcChannel)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun solveMtsp(httpRequest: MtspRequestDto) {
        val rpcRequest = MtspSolverRequest.newBuilder()
            .setRequestId(UUID.randomUUID().toString())
            .addAllCities(
                httpRequest.cities.map { city ->
                    City.newBuilder()
                        .setX(city.x)
                        .setY(city.y)
                        .setName(city.name)
                        .build()
                }
            )
            .setNumSalesmen(httpRequest.salesmanNumber)
            .build()

        coroutineScope.launch {
            try {
                stub.solve(rpcRequest).collect { response ->
                    var logStr = ""
                    for (route in response.routesList) {
                        for (city in route.routeList) {
                            logStr += city.name + " "
                        }
                        logStr += "| "
                    }
                    logger.info { "Received solution: $logStr" }
                }
            } catch (e: Exception) {
                logger.error(e) { "Error calling gRPC server" }
            }
        }
    }

    @PreDestroy
    fun shutdown() {
        grpcChannel.shutdown()
    }
}

//@Service
//class GrpcClientService(
//    private val backendConfig: BackendConfig
//) {
//
//    private val grpcChannel = ManagedChannelBuilder.forAddress(backendConfig.mtsp.host, backendConfig.mtsp.port)
//        .usePlaintext()  // Отключаем шифрование (для теста можно использовать)
//        .build()
//
//    private val tspSolverStub = TspSolverGrpc.newBlockingStub(grpcChannel)
//
//    fun solveTsp(request: TspRequestDto): TspResponseDto {
//        val grpcRequest = TspSolverRequest.newBuilder()
//            .setRequestId(UUID.randomUUID().toString())
//            .addAllCities(request.cities.map { City.newBuilder().setX(it.x).setY(it.y).build() })
//            .build()
//
//        val grpcResponse = tspSolverStub.solve(grpcRequest)
//
//        return TspResponseDto(grpcResponse.routeList.map { Point(it.x, it.y) })
//    }
//
//
//    @PreDestroy
//    fun shutdown() {
//        grpcChannel.shutdown()
//    }
//}
