package api.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant

@Entity
@jakarta.persistence.Table(name = "mtsp_users")
class User() {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    lateinit var organization: Organization

    @Column(name = "first_name", nullable = false)
    lateinit var firstName: String

    @Column(name = "last_name", nullable = false)
    lateinit var lastName: String

    @Column(nullable = false, unique = true)
    lateinit var email: String

    @Column(name = "password_hash", nullable = false)
    lateinit var passwordHash: String

    @Column(name = "is_admin", nullable = false)
    var isAdmin: Boolean = false

    @Column(name = "last_activity", nullable = false)
    var lastActivity: Instant = Instant.now()

    constructor(
        organization: Organization,
        firstName: String,
        lastName: String,
        email: String,
        passwordHash: String,
        isAdmin: Boolean = false,
        lastActivity: Instant = Instant.now()
    ) : this() {
        this.organization = organization
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.passwordHash = passwordHash
        this.isAdmin = isAdmin
        this.lastActivity = lastActivity
    }
}
