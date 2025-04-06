package api.dto

import api.converter.StringArrayConverter
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "mtsp_requests")
class MtspRequest() {
    @Id
    @Column(name = "id", nullable = false)
    var id: String = ""

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SolutionStatus = SolutionStatus.QUEUED

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()

    @Column(name = "salesman_number", nullable = false)
    var salesmanNumber: Long = 0

    @Convert(converter = StringArrayConverter::class)
    @Column(name = "points", nullable = false)
    var points: Array<String> = emptyArray()

    @Column(name = "algorithm", nullable = false)
    var algorithm: String = "bruteForce"

    @Column(name = "algorithm_params")
    var algorithmParams: String? = null

    constructor(
        id: String,
        userId: Long,
        status: SolutionStatus,
        createdAt: Instant,
        salesmanNumber: Long,
        points: Array<String>,
        algorithm: String,
        algorithmParams: String?
    ) : this() {
        this.id = id
        this.userId = userId
        this.status = status
        this.createdAt = createdAt
        this.salesmanNumber = salesmanNumber
        this.points = points
        this.algorithm = algorithm
        this.algorithmParams = algorithmParams
    }
}