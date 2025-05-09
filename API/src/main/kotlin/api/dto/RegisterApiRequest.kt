package api.dto

data class RegisterApiRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val recaptchaToken: String
)
