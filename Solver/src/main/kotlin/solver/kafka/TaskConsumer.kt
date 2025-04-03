package solver.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import solver.service.MtspSolverService
import solver.dto.MtspSolverRequest

@Service
class TaskConsumer(
    private val mtspSolverService: MtspSolverService
) {
    private val objectMapper = jacksonObjectMapper()

    @KafkaListener(topics = [TOPIC], groupId = GROUP_ID)
    suspend fun consumeTask(message: String) {
        logger.info { "Received task: $message" }
        try {
            val request = objectMapper.readValue(message, MtspSolverRequest::class.java)
            logger.info { "[ABOBA] Received task: $request" }
            mtspSolverService.solve(request)
        } catch (e: Exception) {
            logger.error { "[ABOBA] Error parsing Kafka message: ${e.message}" }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val TOPIC = "mtsp-tasks"
        private const val GROUP_ID = "mtsp-group"
    }
}
