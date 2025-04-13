package solver.dto

import jakarta.persistence.*
import solver.converter.StringListConverter

@Entity
@Table(name = "mtsp_maps")
class MtspMap() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(nullable = false)
    var name: String = ""

    @Column(nullable = false)
    var isPublic: Boolean = false

    @Convert(converter = StringListConverter::class)
    @Column(name = "points", nullable = false)
    var cities: List<City> = emptyList()

    @OneToMany(mappedBy = "map", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var edges: List<MtspEdge> = mutableListOf()

    constructor(
        userId: Long,
        name: String,
        cities: List<City>,
        isPublic: Boolean = false
    ) : this() {
        this.userId = userId
        this.name = name
        this.isPublic = isPublic
        this.cities = cities
    }
}