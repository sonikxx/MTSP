package api.repository

import api.dto.MtspMap
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MtspMapRepository : JpaRepository<MtspMap, Long> {
    @Query("SELECT m FROM MtspMap m WHERE m.id = :id AND (m.userId = :userId OR m.isPublic = true)")
    fun findAccessibleMapById(@Param("id") id: Long, @Param("userId") userId: Long): MtspMap?


    @Query("SELECT DISTINCT m FROM MtspMap m WHERE m.userId = :userId OR m.isPublic = true")
    fun findAllByUserIdOrIsPublicTrueDistinct(@Param("userId") userId: Long): List<MtspMap>
}
