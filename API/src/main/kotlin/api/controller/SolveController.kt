package api.controller

import api.dto.*
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

        var solution = mtspSolutionRepository.findFirstByUserIdAndRequestIdAndStatusOrderByTotalCostAsc(userId, requestId, SolutionStatus.SOLVED)
        if (solution == null) {
            solution = mtspSolutionRepository.findFirstByUserIdAndRequestIdOrderByTotalCostAsc(userId, requestId)
        }

        if (solution == null) {
            logger.info { "Solution not found — returning QUEUED with empty routes." }
            return MtspResponse(status = "QUEUED", routes = emptyList())
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
