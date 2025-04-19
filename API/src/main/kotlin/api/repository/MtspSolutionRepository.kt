package api.repository

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

    @Query(
        value = """
        SELECT s.* FROM mtsp_solutions s
        JOIN mtsp_requests r ON s.request_id = r.id
        JOIN mtsp_maps m ON r.map_id = m.id
        WHERE r.map_id = :mapId
          AND s.status = 'SOLVED'
          AND s.total_cost IS NOT NULL
          AND (m.user_id = :userId OR m.is_public = true)
        ORDER BY s.total_cost
        LIMIT 1
    """,
        nativeQuery = true
    )
    fun findBestSolutionForMapById(
        @Param("mapId") mapId: Long,
        @Param("userId") userId: Long
    ): Solution?

    @Query(
        value = "SELECT r.algorithm FROM mtsp_requests r WHERE r.id = :requestId",
        nativeQuery = true
    )
    fun findAlgorithmByRequestId(@Param("requestId") requestId: String): String?
}
