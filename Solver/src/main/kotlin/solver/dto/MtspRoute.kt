package solver.dto

import jakarta.persistence.*
import solver.converter.StringArrayConverter

@Entity
@Table(name = "mtsp_routes")
class MtspRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    lateinit var solution: MtspSolution

    @Column(name = "salesman_index", nullable = false)
    var salesmanIndex: Int = 0

    @Convert(converter = StringArrayConverter::class)
    @Column(name = "points", nullable = false)
    var points: Array<String> = emptyArray()
}