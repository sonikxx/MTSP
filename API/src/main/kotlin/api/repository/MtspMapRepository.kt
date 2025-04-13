package api.repository

import api.dto.MtspMap
import org.springframework.data.jpa.repository.JpaRepository

interface MtspMapRepository : JpaRepository<MtspMap, Long> {
    fun findByIdAndUserId(id: Long, userId: Long): MtspMap?
}