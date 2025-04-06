package api.repository

import api.dto.MtspRequest
import api.dto.Solution
import org.springframework.data.jpa.repository.JpaRepository

interface MtspRequestRepository: JpaRepository<MtspRequest, Long>  {
}