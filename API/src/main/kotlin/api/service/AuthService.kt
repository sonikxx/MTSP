package api.service

import api.dto.UserDto
import api.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository
) {

    fun login(
        email: String,
        password: String
    ): UserDto? {
        val user = userRepository.findByEmail(email)
        return if (user != null && user.passwordHash == password) user else null
    }
}
