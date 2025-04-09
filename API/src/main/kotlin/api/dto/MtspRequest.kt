package api.dto

import api.converter.StringListConverter
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.QUEUED

    @Column(name = "salesman_number", nullable = false)
    var salesmanNumber: Long = 0

    @Convert(converter = StringListConverter::class)
    @Column(name = "points", nullable = false)
    var points: List<City> = emptyList()

    @Column(nullable = false, length = 50)
    lateinit var algorithm: String

    @Column(name = "algorithm_params", columnDefinition = "TEXT")
    var algorithmParams: String? = null

    @OneToMany(mappedBy = "request", cascade = [CascadeType.ALL], orphanRemoval = false, fetch = FetchType.LAZY)
    var edges: MutableList<MtspEdge> = mutableListOf()

    constructor(
        userId: Long,
        salesmanNumber: Long,
        points: List<City>,
        algorithm: String,
        algorithmParams: String? = null,
        status: RequestStatus = RequestStatus.QUEUED
    ) : this() {
        this.userId = userId
        this.salesmanNumber = salesmanNumber
        this.points = points
        this.algorithm = algorithm
        this.algorithmParams = algorithmParams
        this.status = status
    }

    fun addEdge(edge: MtspEdge) {
        edge.request = this
        edges.add(edge)
    }
}
