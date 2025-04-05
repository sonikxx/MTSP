package api.repository

import api.dto.Route
import api.dto.Solution
import api.dto.SolutionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MtspSolutionRepository : JpaRepository<Solution, Long> {
    fun findFirstByUserIdAndRequestIdAndStatusOrderByTotalCostAsc(
        userId: Long,
        requestId: String,
        status: SolutionStatus
    ): Solution?

    fun findFirstByUserIdAndRequestIdOrderByTotalCostAsc(
        userId: Long,
        requestId: String
    ): Solution?
}
