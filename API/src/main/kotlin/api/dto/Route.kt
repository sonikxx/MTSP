package api.dto

import api.converter.StringArrayConverter
import jakarta.persistence.*


@Entity
@Table(name = "mtsp_routes")
class Route() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    lateinit var solution: Solution

    @Column(name = "salesman_index", nullable = false)
    var salesmanIndex: Int = 0

    @Convert(converter = StringArrayConverter::class)
    @Column(name = "points", nullable = false)
    var points: Array<String> = emptyArray()

    constructor(
        solution: Solution,
        salesmanIndex: Int,
        points: Array<String>
    ) : this() {
        this.solution = solution
        this.salesmanIndex = salesmanIndex
        this.points = points
    }
}