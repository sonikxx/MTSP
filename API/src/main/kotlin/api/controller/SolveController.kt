package api.controller

import api.dto.MtspApiRequest
import api.dto.MtspResponseAccept
import api.dto.MtspRequest
import api.dto.MtspEdge
import api.dto.MtspResponse
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

        // TODO: may be it is possible to factor out in some sort of converter
        val mtspRequest = MtspRequest(
            userId = userId,
            salesmanNumber = request.salesmanNumber,
            points = request.cities.map { it.toString() }.toTypedArray(),
            algorithm = request.algorithm,
            algorithmParams = request.algorithmParams.toString()
        )
        request.distances.mapIndexed { fromNode, edge ->
            edge.mapIndexed { toNode, distance ->
                mtspRequest.addEdge(MtspEdge(
                        request = mtspRequest,
                        fromNode = fromNode.toLong(),
                        toNode = toNode.toLong(),
                        distance = distance
                    )
                )
            }
        }
        requestRepository.save(mtspRequest)

        requestProducer.sendTask(mtspRequest.id)

        return MtspResponseAccept(mtspRequest.id)
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
