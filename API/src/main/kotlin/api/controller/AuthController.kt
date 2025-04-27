package api.controller

import api.config.JwtProperties
import api.dto.LoginApiRequest
import api.dto.RegisterApiRequest
import api.security.JwtTokenUtil
import api.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val jwtProperties: JwtProperties,
    private val authService: AuthService
) {

    private val recaptchaUrl = "https://www.google.com/recaptcha/api/siteverify"
    private val recaptcha1 = "6LcxbSYrAAAAAI8i3nFy"
    private val recaptcha2 = "nPMHc6MH8MYO_CJrbkSO"

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterApiRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {

        val isRecaptchaValid = verifyRecaptcha(request.recaptchaToken)
        if (!isRecaptchaValid) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("reCAPTCHA verification failed. Please try again.")
        }

        val user = authService.register(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = request.password
        ) ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists")

        val token = jwtTokenUtil.generateToken(user.id, user.email)
        val cookie = Cookie(jwtProperties.cookie.name, token).apply {
            path = jwtProperties.cookie.path
            isHttpOnly = jwtProperties.cookie.isHttpOnly
            secure = jwtProperties.cookie.secure
            maxAge = jwtProperties.cookie.maxAge
        }
        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/create")
            .build()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginApiRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        val user = authService.login(
            email = request.email,
            password = request.password
        ) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")

        val token = jwtTokenUtil.generateToken(user.id, user.email)
        val cookie = Cookie(jwtProperties.cookie.name, token).apply {
            path = jwtProperties.cookie.path
            isHttpOnly = jwtProperties.cookie.isHttpOnly
            secure = jwtProperties.cookie.secure
            maxAge = jwtProperties.cookie.maxAge
        }
        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/create")
            .build()
    }

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<String> {
        val cookie = Cookie(jwtProperties.cookie.name, "").apply {
            path = jwtProperties.cookie.path
            isHttpOnly = jwtProperties.cookie.isHttpOnly
            secure = jwtProperties.cookie.secure
            maxAge = 0
        }

        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/")
            .build()
    }



    private fun verifyRecaptcha(recaptchaToken: String): Boolean {
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.contentType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED

        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("secret", recaptcha1 + recaptcha2)
        params.add("response", recaptchaToken)

        val entity = HttpEntity(params, headers)
        val verifyResponse = restTemplate.postForObject(recaptchaUrl, entity, String::class.java)
        return verifyResponse?.contains("\"success\": true") == true
    }
}
