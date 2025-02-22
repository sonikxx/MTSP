package Api

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.stereotype.Service
import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpc
import jakarta.annotation.PreDestroy

@Service
class GrpcClientService {

    private val grpcChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext()  // Отключаем шифрование (для теста можно использовать)
        .build()

    private val echoServiceStub = EchoServiceGrpc.newBlockingStub(grpcChannel)

    fun getEchoMessage(message: String): String {
        val request = EchoRequest.newBuilder()
            .setMessage(message)
            .build()

        val response = echoServiceStub.sayEcho(request)
        return response.message
    }

    // Не забудьте закрыть канал при завершении работы приложения
    @PreDestroy
    fun shutdown() {
        grpcChannel.shutdown()
    }
}
