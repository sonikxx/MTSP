package api.dto

import java.time.LocalDateTime

data class UserDto(
    val id: Long = 0,
    val organizationId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val passwordHash: String,
    var isAdmin: Boolean = false,
    val lastActivity: LocalDateTime = LocalDateTime.now()
)
