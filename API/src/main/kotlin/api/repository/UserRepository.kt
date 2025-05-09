package api.repository

import api.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

//    fun findAllByIdIn(ids: List<Long>): List<User>
}
