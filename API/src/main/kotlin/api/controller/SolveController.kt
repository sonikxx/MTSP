package api.controller

import api.dto.MtspRequest
import api.dto.MtspResponse
import api.dto.MtspResponseAccept
import api.dto.MtspSolverRequest
import api.kafka.RequestProducer
import api.service.SolutionService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/protected")
class SolveController(
    private val requestProducer: RequestProducer,
    private val solutionService: SolutionService
) {

    @PostMapping("/v1/solve")
    fun solve(
        @RequestBody request: MtspRequest,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspResponseAccept {
        val requestId = UUID.randomUUID().toString()

        requestProducer.sendTask(
            MtspSolverRequest(
                requestId,
                userId,
                request.cities,
                request.salesmanNumber,
                "bruteForce",
            )
        )
        return MtspResponseAccept(requestId)
    }

    @GetMapping("/v1/result/{requestId}")
    fun getStatus(
        @PathVariable requestId: String,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspResponse {
        logger.info { "Received request!" }

        val solution = solutionService.getSolution(requestId, userId)
        if (solution == null) {
            logger.info { "Solution not found — returning QUEUED with empty routes." }
            return MtspResponse(status = "QUEUED")
        }

        val routes = solution.routes
            .sortedBy { it.salesmanIndex }
            .map { it.points.toList() }

        logger.info { "Found solution with status=${solution.status}, routes count=${routes.size}" }

        return MtspResponse(
            status = solution.status.name,
            routes = routes
        )
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
