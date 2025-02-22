package Solver

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpc
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class EchoService : EchoServiceGrpc.EchoServiceImplBase() {
    override fun sayEcho(request: EchoRequest, responseObserver: StreamObserver<EchoResponse>) {
        // Логика обработки запроса и отправки ответа
        val response = EchoResponse.newBuilder()
            .setMessage("Echo: ${request.message}")
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
