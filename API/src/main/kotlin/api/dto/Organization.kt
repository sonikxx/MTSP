package api.dto

import api.converter.StringArrayConverter
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "mtsp_organizations")
class Organization() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false, unique = true)
    lateinit var name: String

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()

    constructor(name: String) : this() {
        this.name = name
    }
}

