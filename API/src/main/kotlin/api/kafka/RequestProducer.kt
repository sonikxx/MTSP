package api.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class RequestProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val topic = "mtsp-tasks"

    fun sendTask(task: String) {
        kafkaTemplate.send(topic, task)
    }
}