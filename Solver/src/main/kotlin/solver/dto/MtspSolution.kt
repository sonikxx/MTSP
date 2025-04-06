package solver.dto

import jakarta.persistence.*
import java.time.Instant

enum class SolutionStatus {
    QUEUED, INTERMEDIATE, SOLVED, FAILED
}

@Entity
@Table(name = "mtsp_solutions")
class MtspSolution() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(name = "request_id", nullable = false)
    var requestId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SolutionStatus = SolutionStatus.QUEUED

    @Column(name = "total_cost")
    var totalCost: Double? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()

    @Column(name = "completed_at")
    var completedAt: Instant? = null

    @OneToMany(mappedBy = "solution", cascade = [CascadeType.ALL], orphanRemoval = false)
    var routes: MutableList<MtspRoute> = mutableListOf()

    constructor(
        userId: Long,
        requestId: String,
        status: SolutionStatus = SolutionStatus.QUEUED,
        totalCost: Double? = null,
        createdAt: Instant = Instant.now(),
        completedAt: Instant? = null
    ) : this() {
        this.userId = userId
        this.requestId = requestId
        this.status = status
        this.totalCost = totalCost
        this.createdAt = createdAt
        this.completedAt = completedAt
    }


    fun addRoute(route: MtspRoute) {
        route.solution = this
        routes.add(route)
    }
}