package api.controller

import api.dto.MtspRequest
import api.dto.MtspResponse
import api.dto.MtspResponseAccept
import api.dto.MtspSolverRequest
import api.kafka.RequestProducer
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/protected")
class SolveController(private val requestProducer: RequestProducer) {

    @PostMapping("/v1/solve")
    fun solve(@RequestBody request: MtspRequest): MtspResponseAccept {
        val requestId = UUID.randomUUID().toString()
        logger.info { "Sending request to backend with id $requestId" }

        requestProducer.sendTask(MtspSolverRequest(requestId, request.cities, request.salesmanNumber))
        return MtspResponseAccept(requestId)
    }

    @GetMapping("/v1/result/{requestId}")
    fun getStatus(@PathVariable requestId: String): MtspResponse {
        logger.info { "Received request!" }
        if ((0 until 10).random() == 3) {
            return MtspResponse(status = "SOLVED", routes = listOf(listOf("A", "B"), listOf("C", "D", "E")))
        }
        return MtspResponse(routes = listOf(listOf("A", "B", "E"), listOf("C", "D")))
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
