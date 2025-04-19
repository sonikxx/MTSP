package api.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

enum class RequestStatus {
    QUEUED, SOLVED, CANCELED, FAILED
}

@Entity
@Table(name = "mtsp_requests")
class MtspRequest() {

    @Id
    @Column(length = 40)
    var id: String = UUID.randomUUID().toString()

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.QUEUED

    @Column(name = "map_id", nullable = false)
    var mapId: Long = 0

    @Column(name = "salesman_number", nullable = false)
    var salesmanNumber: Long = 0

    @Column(nullable = false, length = 50)
    lateinit var algorithm: String

    @Column(name = "algorithm_params", columnDefinition = "TEXT")
    var algorithmParams: String? = null

    constructor(
        userId: Long,
        salesmanNumber: Long,
        mapId: Long,
        algorithm: String,
        algorithmParams: String? = null,
        status: RequestStatus = RequestStatus.QUEUED
    ) : this() {
        this.userId = userId
        this.salesmanNumber = salesmanNumber
        this.mapId = mapId
        this.algorithm = algorithm
        this.algorithmParams = algorithmParams
        this.status = status
    }
}
