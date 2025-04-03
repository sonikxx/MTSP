package api.kafka

import api.dto.MtspSolverRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class RequestProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {
    private val objectMapper = jacksonObjectMapper()

    fun sendTask(request: MtspSolverRequest) {
        logger.info { "Sending request to backend with id $request.requestId" }

        try {
            val message = objectMapper.writeValueAsString(request)
            kafkaTemplate.send(ProducerRecord(TOPIC, request.requestId, message))
            logger.info { "Message sent to Kafka topic $TOPIC" }
        } catch (e: Exception) {
            logger.error { "Error sending Kafka message: ${e.message}" }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val TOPIC = "mtsp-tasks"
    }
}
