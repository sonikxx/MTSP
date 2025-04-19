package api.controller

import api.dto.MtspApiRequest
import api.dto.MtspApiResponse
import api.dto.MtspResponseAccept
import api.kafka.RequestProducer
import api.repository.MtspRequestRepository
import api.service.SolutionService
import api.service.TaskService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/protected/v1")
class SolveController(
    private val taskService: TaskService,
    private val requestProducer: RequestProducer,
    private val solutionService: SolutionService,
    private val requestRepository: MtspRequestRepository
) {

    @PostMapping("solve")
    fun solve(
        @RequestBody request: MtspApiRequest,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspResponseAccept {
        val mtspRequest = taskService.createMtspRequest(userId, request)

        requestRepository.save(mtspRequest)

        requestProducer.sendTask(mtspRequest.id)

        return MtspResponseAccept(mtspRequest.id)
    }

    @GetMapping("result/{requestId}")
    fun getStatus(
        @PathVariable requestId: String,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspApiResponse {
        logger.info { "Received request!" }

        val solution = solutionService.getSolution(requestId, userId)
        if (solution == null) {
            logger.info { "Solution not found — returning QUEUED with empty routes." }
            return MtspApiResponse(status = "QUEUED")
        }

        val routes = solution.routes
            .sortedBy { it.salesmanIndex }
            .map { it.points.toList() }

        return MtspApiResponse(
            status = solution.status.name,
            routes = routes,
            totalCost = solution.totalCost ?: 0.0,
            totalTime = (solution.completedAt ?: Instant.now()).toEpochMilli().minus(solution.createdAt.toEpochMilli())
        )
    }

    @GetMapping("best/{mapId}")
    fun getBest(
        @PathVariable mapId: Long,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspApiResponse {
        logger.info { "Received request!" }

        val solution = solutionService.getBestSolutionForMap(mapId, userId)
        if (solution == null) {
            logger.info { "Solution not found — returning QUEUED with empty routes." }
            return MtspApiResponse(status = "QUEUED")
        }
        val routes = solution.routes
            .sortedBy { it.salesmanIndex }
            .map { it.points.toList() }
        return MtspApiResponse(
            status = solution.status.name,
            routes = routes,
            totalCost = solution.totalCost ?: 0.0,
            totalTime = (solution.completedAt ?: Instant.now()).toEpochMilli().minus(solution.createdAt.toEpochMilli())
        )
    }

    @DeleteMapping("solve/{requestId}")
    fun cancelTask(
        @PathVariable requestId: String,
        @RequestAttribute(name = "userId") userId: Long
    ): ResponseEntity<Void> {
        logger.info { "Received request to cancel task $requestId" }
        val canceled = taskService.cancelTask(requestId, userId)
        return if (canceled) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
