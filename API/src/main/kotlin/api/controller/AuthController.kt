package api.controller

import api.dto.LoginRequest
import api.security.JwtTokenUtil
import api.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        val user = authService.login(
            email = request.email,
            password = request.password
        ) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")

        val token = jwtTokenUtil.generateToken(user.id, user.organizationId, user.isAdmin)
        val cookie = Cookie("JWT", token).apply {
            path = "/"
            isHttpOnly = true
            secure = true
            maxAge = 3600 // 1 час
        }
        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/main/${user.organizationId}")
            .build()
    }

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<String> {
        val cookie = Cookie("JWT", "").apply {
            path = "/"
            isHttpOnly = true
            secure = true
            maxAge = 0
        }

        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/")
            .build()
    }
}
