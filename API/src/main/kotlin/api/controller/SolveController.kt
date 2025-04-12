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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant


@RestController
@RequestMapping("/protected")
class SolveController(
    private val taskService: TaskService,
    private val requestProducer: RequestProducer,
    private val solutionService: SolutionService,
    private val requestRepository: MtspRequestRepository
) {

    @PostMapping("/v1/solve")
    fun solve(
        @RequestBody request: MtspApiRequest,
        @RequestAttribute(name = "userId") userId: Long
    ): MtspResponseAccept {
        val mtspRequest = taskService.createMtspRequest(userId, request)

        requestRepository.save(mtspRequest)

        requestProducer.sendTask(mtspRequest.id)

        return MtspResponseAccept(mtspRequest.id)
    }

    @GetMapping("/v1/result/{requestId}")
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
            totalCost = solution.totalCost?: 0.0,
            totalTime = (solution.completedAt?: Instant.now()).toEpochMilli().minus(solution.createdAt.toEpochMilli())
        )
    }

    @DeleteMapping("/v1/solve/{requestId}")
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
