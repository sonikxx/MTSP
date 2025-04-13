package api.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "mtsp_edges")
class MtspEdge() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", nullable = false)
    lateinit var map: MtspMap

    @Column(name = "from_node", nullable = false)
    var fromNode: Int = 0

    @Column(name = "to_node", nullable = false)
    var toNode: Int = 0

    @Column(nullable = false)
    var distance: Double = 0.0

    constructor(
        map: MtspMap,
        fromNode: Int,
        toNode: Int,
        distance: Double
    ) : this() {
        this.map = map
        this.fromNode = fromNode
        this.toNode = toNode
        this.distance = distance
    }
}
