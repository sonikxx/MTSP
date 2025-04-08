package solver.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import solver.dto.MtspRequest

@Repository
interface MtspRequestRepository : JpaRepository<MtspRequest, String> {
    @EntityGraph(attributePaths = ["edges"])
    fun findWithEdgesById(id: String): MtspRequest?
}