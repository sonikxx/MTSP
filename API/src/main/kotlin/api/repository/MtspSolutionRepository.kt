package api.repository

import api.dto.Route
import api.dto.Solution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MtspSolutionRepository : JpaRepository<Solution, Long> {
    // TODO: return full solution not just routes. Total distance and may be some other info
    @Query("""
        SELECT r.* FROM mtsp_routes r
        WHERE r.solution_id = (
            SELECT s.id FROM mtsp_solutions s
            WHERE s.user_id = :userId
            ORDER BY s.total_cost ASC
            LIMIT 1
        )
    """, nativeQuery = true)
    fun findBestRoute(@Param("userId") userId: Long): List<Route>
}