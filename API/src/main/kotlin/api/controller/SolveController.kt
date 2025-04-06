package api.controller

import api.dto.*
import api.kafka.RequestProducer
import api.repository.MtspRequestRepository
import api.service.SolutionService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/protected")
class SolveController(
    private val requestProducer: RequestProducer,
    private val solutionService: SolutionService,
    private val requestRepository: MtspRequestRepository,
) {

    @PostMapping("/v1/solve")
    fun solve(
        @RequestBody request: MtspApiRequest,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspResponseAccept {
        val requestId = UUID.randomUUID().toString()

        requestProducer.sendTask(
            MtspSolverRequest(
                requestId = requestId,
                userId = userId,
                cities = request.cities,
                numSalesmen = request.salesmanNumber,
                algorithm = request.algorithm,
                algorithmParams = request.algorithmParams,
            )
        )
        requestRepository.save(
            MtspRequest(
                id = requestId,
                userId = userId,
                status = SolutionStatus.QUEUED,
                createdAt = Instant.now(),
                salesmanNumber = request.salesmanNumber,
                points = request.cities.map { it.toString() }.toTypedArray(),
                algorithm = request.algorithm,
                algorithmParams = request.algorithmParams.toString(),
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
