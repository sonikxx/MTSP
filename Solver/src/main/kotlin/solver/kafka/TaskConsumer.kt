package solver.kafka

import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import solver.dto.RequestStatus
import solver.service.MtspSolverService
import solver.repository.MtspRequestRepository

@Service
class TaskConsumer(
    private val mtspSolverService: MtspSolverService,
    private val mtspRequestRepository: MtspRequestRepository,
) {
    @KafkaListener(topics = [TOPIC], groupId = GROUP_ID)
    suspend fun consumeTask(requestId: String) {
        // TODO: only one consumer for now
        logger.info { "Received task: $requestId" }

        mtspRequestRepository.findWithMapAndEdgesById(requestId)?.let { request ->
            if (request.status == RequestStatus.CANCELED) {
                logger.info { "Task is already canceled: $request" }
                return
            }
            logger.info { "Solving task: $request" }
            mtspSolverService.solve(request)
        } ?: logger.error { "No MTSP request found for ID: $requestId" }
        // TODO: save error in database if it is possible
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val TOPIC = "mtsp-tasks"
        private const val GROUP_ID = "mtsp-group"
    }
}
