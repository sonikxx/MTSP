package solver.dto

import jakarta.persistence.*
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", nullable = false)
    lateinit var map: MtspMap

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RequestStatus = RequestStatus.QUEUED

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now()

    @Column(name = "salesman_number", nullable = false)
    var salesmanNumber: Int = 0

    @Column(nullable = false)
    lateinit var algorithm: String

    @Column(name = "algorithm_params", columnDefinition = "TEXT")
    var algorithmParams: String? = null
}
