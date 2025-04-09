package api.dto

import api.converter.StringListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "mtsp_routes")
class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    lateinit var solution: Solution

    @Column(name = "salesman_index", nullable = false)
    var salesmanIndex: Int = 0

    @Convert(converter = StringListConverter::class)
    @Column(name = "points", nullable = false)
    var points: List<City> = emptyList()
}
