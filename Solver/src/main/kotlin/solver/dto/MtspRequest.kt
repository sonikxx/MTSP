package solver.dto

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.OneToMany
import jakarta.persistence.CascadeType
import jakarta.persistence.FetchType
import solver.converter.StringListConverter
import java.time.Instant

enum class RequestStatus {
    QUEUED, SOLVED, CANCELED, FAILED
}

@Entity
@Table(name = "mtsp_requests")
class MtspRequest {

    @Id
    @Column(length = 40)
    lateinit var id: String

    @Column(name = "user_id", nullable = false)
    var userId: Int = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RequestStatus = RequestStatus.QUEUED

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now()

    @Column(name = "salesman_number", nullable = false)
    var salesmanNumber: Int = 0

    @Convert(converter = StringListConverter::class)
    @Column(name = "points", nullable = false)
    var points: List<String> = emptyList()

    @Column(nullable = false)
    lateinit var algorithm: String

    @Column(name = "algorithm_params", columnDefinition = "TEXT")
    var algorithmParams: String? = null

    @OneToMany(mappedBy = "request", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var edges: List<MtspEdge> = mutableListOf()
}
