package solver.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import solver.dto.MtspSolution

@Repository
interface MtspSolutionRepository : JpaRepository<MtspSolution, Long> {
}