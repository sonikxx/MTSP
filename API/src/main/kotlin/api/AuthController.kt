package api
import api.dto.LoginRequest
import api.security.JwtTokenUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<String> {
        val token = jwtTokenUtil.generateToken(request.userId, request.organizationId, request.isAdmin)

        val cookie = Cookie("JWT", token).apply {
            path = "/"
            isHttpOnly = true
            secure = true
            maxAge = 3600 // 1 час
        }

        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/main/${request.organizationId}")
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