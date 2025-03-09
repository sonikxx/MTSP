package api

import api.dto.Point
import api.dto.TspRequestDto
import api.dto.TspResponseDto
import com.example.grpc.City
import io.grpc.ManagedChannelBuilder
import org.springframework.stereotype.Service
import com.example.grpc.TspSolverRequest
import mu.KotlinLogging
import com.example.grpc.TspSolverGrpc
import jakarta.annotation.PreDestroy
import java.util.*


@Service
class GrpcClientService {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val grpcChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext()  // Отключаем шифрование (для теста можно использовать)
        .build()

    private val tspSolverStub = TspSolverGrpc.newBlockingStub(grpcChannel)

    fun solveTsp(request: TspRequestDto): TspResponseDto {
        val grpcRequest = TspSolverRequest.newBuilder()
            .setRequestId(UUID.randomUUID().toString())
            .addAllCities(request.cities.map { City.newBuilder().setX(it.x).setY(it.y).build() })
            .build()

        val grpcResponse = tspSolverStub.solve(grpcRequest)

        return TspResponseDto(grpcResponse.routeList.map { Point(it.x, it.y) })
    }


    // Не забудьте закрыть канал при завершении работы приложения
    @PreDestroy
    fun shutdown() {
        grpcChannel.shutdown()
    }
}
