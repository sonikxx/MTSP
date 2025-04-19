package api.service

import api.dto.User
import api.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(email: String, password: String): User? {
        val user = userRepository.findByEmail(email)
        return if (user != null && passwordEncoder.matches(password, user.passwordHash)) user else null
    }

    fun register(firstName: String, lastName: String, email: String, password: String): User? {
        if (userRepository.findByEmail(email) != null) return null

        val user = User().apply {
            this.firstName = firstName
            this.lastName = lastName
            this.email = email
            this.passwordHash = passwordEncoder.encode(password)
            this.lastActivity = Instant.now()
        }

        return userRepository.save(user)
    }
}
