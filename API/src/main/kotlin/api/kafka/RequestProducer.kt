package api.kafka

import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class RequestProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    fun sendTask(requestId: String) {
        logger.info { "Sending request to backend with id $requestId" }

        try {
            kafkaTemplate.send(ProducerRecord(TOPIC, requestId, requestId))
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
