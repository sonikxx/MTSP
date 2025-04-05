package api.dto

import jakarta.persistence.*
import java.time.Instant


enum class SolutionStatus {
    QUEUED, INTERMEDIATE, SOLVED, FAILED
}

@Entity
@Table(name = "mtsp_solutions")
class Solution() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "request_id", nullable = false)
    var requestId: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

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
    var routes: MutableList<Route> = mutableListOf()

    constructor(
        user: User,
        status: SolutionStatus = SolutionStatus.QUEUED,
        totalCost: Double? = null,
        createdAt: Instant = Instant.now(),
        completedAt: Instant? = null
    ) : this() {
        this.user = user
        this.status = status
        this.totalCost = totalCost
        this.createdAt = createdAt
        this.completedAt = completedAt
    }
}