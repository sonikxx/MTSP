package api.service

import api.dto.User
import api.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository
) {

    fun login(email: String, password: String): User? {
        val user = userRepository.findByEmail(email)
        // TODO: hash password after test all application logic
        return if (user != null && user.passwordHash == password) user else null
    }
}
