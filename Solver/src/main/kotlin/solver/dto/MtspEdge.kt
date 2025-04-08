package solver.dto

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType


@Entity
@Table(name = "mtsp_edges")
class MtspEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    lateinit var request: MtspRequest

    @Column(name = "from_node", nullable = false)
    var fromNode: Int = 0

    @Column(name = "to_node", nullable = false)
    var toNode: Int = 0

    @Column(nullable = false)
    var distance: Double = 0.0
}