package api.dto

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

enum class SolutionStatus {
    QUEUED, INTERMEDIATE, SOLVED, FAILED
}

@Entity
@Table(name = "mtsp_solutions")
class Solution {
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
}
