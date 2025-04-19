package api.service

import api.dto.Solution
import api.dto.SolutionStatus
import api.repository.MtspSolutionRepository
import org.springframework.stereotype.Service

@Service
class SolutionService(
    private val solutionRepository: MtspSolutionRepository
) {
    fun getSolution(requestId: String, userId: Long): Solution? =
        solutionRepository.findFirstByUserIdAndRequestIdAndStatusOrderByTotalCostAsc(
            userId,
            requestId,
            SolutionStatus.SOLVED
        ) ?: solutionRepository.findFirstByUserIdAndRequestIdOrderByTotalCostAsc(userId, requestId)

    fun getBestSolutionForMap(mapId: Long, userId: Long): Solution?  {
        return solutionRepository.findBestSolutionForMapById(mapId, userId)
    }
}
