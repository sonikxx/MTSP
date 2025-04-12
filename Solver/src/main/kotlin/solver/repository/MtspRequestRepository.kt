package solver.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import solver.dto.MtspRequest
import solver.dto.RequestStatus

@Repository
interface MtspRequestRepository : JpaRepository<MtspRequest, String> {
    @EntityGraph(attributePaths = ["edges"])
    fun findWithEdgesById(id: String): MtspRequest?

    @Query("SELECT r.status FROM MtspRequest r WHERE r.id = :id")
    fun findStatusById(@Param("id") id: String): RequestStatus?

    @Transactional
    @Modifying
    @Query("UPDATE MtspRequest r SET r.status = :status WHERE r.id = :id")
    fun updateStatusById(@Param("id") id: String, @Param("status") status: RequestStatus)
}