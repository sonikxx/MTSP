package api.dto

import api.converter.StringListConverter
import jakarta.persistence.*
import java.time.Instant


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
    var points: List<City> = emptyList()

    @OneToMany(mappedBy = "map", cascade = [CascadeType.ALL], orphanRemoval = false, fetch = FetchType.LAZY)
    var edges: MutableList<MtspEdge> = mutableListOf()

    @Column(name = "created_at", updatable = false, insertable = false, nullable = false)
    var createdAt: Instant = Instant.now()

    constructor(
        userId: Long,
        name: String,
        points: List<City>,
        isPublic: Boolean = false
    ) : this() {
        this.userId = userId
        this.name = name
        this.isPublic = isPublic
        this.points = points
    }

    fun addEdge(edge: MtspEdge) {
        edge.map = this
        edges.add(edge)
    }
}
