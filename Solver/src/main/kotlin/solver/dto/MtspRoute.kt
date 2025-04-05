package solver.dto

import jakarta.persistence.*
import solver.converter.StringListConverter

@Entity
@Table(name = "mtsp_routes")
class MtspRoute() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    lateinit var solution: MtspSolution

    @Column(name = "salesman_index", nullable = false)
    var salesmanIndex: Int = 0

    @Convert(converter = StringListConverter::class)
    @Column(name = "points", nullable = false)
    var points: List<String> = emptyList()

    constructor(
        solution: MtspSolution,
        salesmanIndex: Int = 0,
        points: List<String> = emptyList()
    ) : this() {
        this.solution = solution
        this.salesmanIndex = salesmanIndex
        this.points = points
    }
}