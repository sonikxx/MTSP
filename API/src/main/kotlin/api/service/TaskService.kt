package api.service

import api.dto.MtspApiRequest
import api.dto.MtspRequest
import api.dto.RequestStatus
import api.repository.MtspRequestRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val mtspRequestRepository: MtspRequestRepository
) {

    fun createMtspRequest(userId: Long, apiRequest: MtspApiRequest): MtspRequest {
        val mtspRequest = MtspRequest(
            userId = userId,
            salesmanNumber = apiRequest.salesmanNumber,
            mapId = apiRequest.mapId,
            algorithm = apiRequest.algorithm,
            algorithmParams = apiRequest.algorithmParams.toString()
        )

        return mtspRequestRepository.save(mtspRequest)
    }

    fun cancelTask(requestId: String, userId: Long): Boolean {
        val request = mtspRequestRepository.findById(requestId).orElse(null) ?: return false

        if (request.userId != userId) {
            logger.warn { "User with id $userId tried to cancel task with id $requestId" }
            return false
        }

        if (request.status in listOf(RequestStatus.SOLVED, RequestStatus.CANCELED)) {
            logger.warn { "Task with id $requestId is already solved or canceled" }
            return false
        }

        request.status = RequestStatus.CANCELED
        mtspRequestRepository.save(request)
        return true
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
