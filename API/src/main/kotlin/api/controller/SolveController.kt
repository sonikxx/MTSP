package api.controller

import api.dto.MtspRequest
import api.dto.MtspResponse
import api.dto.MtspResponseAccept
import api.dto.MtspSolverRequest
import api.kafka.RequestProducer
import api.repository.MtspSolutionRepository
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/protected")
class SolveController(
    private val requestProducer: RequestProducer,
    private val mtspSolutionRepository: MtspSolutionRepository
) {

    @PostMapping("/v1/solve")
    fun solve(@RequestBody request: MtspRequest): MtspResponseAccept {
        val requestId = UUID.randomUUID().toString()

        requestProducer.sendTask(MtspSolverRequest(requestId, request.cities, request.salesmanNumber))
        return MtspResponseAccept(requestId)
    }

    @GetMapping("/v1/result/{requestId}")
    fun getStatus(
        @PathVariable requestId: String,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspResponse {
        logger.info { "Received request!" }

        val routes = mtspSolutionRepository.findBestRoute(userId)

        for (route in routes) {
            logger.info { "Route:" }
            for (point in route.points) {
                logger.info { point }
            }
            logger.info { "======= ======" }
        }
        return MtspResponse(status = "SOLVED", routes = listOf(listOf("A", "B"), listOf("C", "D", "E")))
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
