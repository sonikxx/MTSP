package api.service

import api.dto.User
import api.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository
) {

    fun login(email: String, password: String): User? {
        val user = userRepository.findByEmail(email)
        // TODO: hash password after test all application logic
        return if (user != null && user.passwordHash == password) user else null
    }

    fun register(firstName: String, lastName: String, email: String, password: String): User? {
        if (userRepository.findByEmail(email) != null) return null

        val user = User().apply {
            this.firstName = firstName
            this.lastName = lastName
            this.email = email
            this.passwordHash = password // TODO: replace with hashed password later
            this.lastActivity = Instant.now()
        }

        return userRepository.save(user)
    }
}
