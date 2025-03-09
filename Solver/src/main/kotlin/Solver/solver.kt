package Solver

import com.example.grpc.City
import com.example.grpc.TspSolverRequest
import com.example.grpc.TspSolverResponse
import com.example.grpc.TspSolverGrpc
import io.grpc.stub.StreamObserver
import mu.KotlinLogging
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class TspSolverService(private val tspAlgorithm: TspAlgorithm) : TspSolverGrpc.TspSolverImplBase() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun solve(request: TspSolverRequest, responseObserver: StreamObserver<TspSolverResponse>) {
        logger.info { "$request.requestId: Grpc Accepted request for solving!" }
        val cities = request.citiesList.map { Point(it.x, it.y) }
        val route = tspAlgorithm.bruteForceTsp(request.requestId, cities)

        val response = TspSolverResponse.newBuilder()
            .addAllRoute(route.map { City.newBuilder().setX(it.x).setY(it.y).build() })
            .build()

        logger.info { "Grpc Sending response for solving!" }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
