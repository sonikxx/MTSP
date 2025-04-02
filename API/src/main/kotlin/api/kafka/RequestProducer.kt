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
    private val topic = "mtsp-tasks"

    fun sendTask(request: MtspSolverRequest) {
        try {
            val message = objectMapper.writeValueAsString(request)
            kafkaTemplate.send(ProducerRecord(topic, request.requestId, message))
            logger.info { "Message sent to Kafka topic $topic" }
        } catch (e: Exception) {
            logger.error { "Error sending Kafka message: ${e.message}" }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }

}