package api.dto

data class LoginRequest(val userId: Long, val organizationId: Long, val isAdmin: Boolean)
