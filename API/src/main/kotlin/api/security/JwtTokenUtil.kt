package api.security
import api.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenUtil(
    private val jwtProperties: JwtProperties
) {
    private val key = Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray())

    fun generateToken(userId: Long, username: String): String {
        return Jwts.builder()
            .claims(
                mutableMapOf(
                    USER_ID to userId,
                    USER_NAME to username
                )
            )
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtProperties.cookie.maxAge))
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: JwtException) {
            false
        }
    }

    fun getToken(request: HttpServletRequest): String? {
        val token = request.cookies?.firstOrNull { it.name == jwtProperties.cookie.name }?.value

        if (token.isNullOrBlank() || !validateToken(token)) {
            return null
        }
        return token
    }

    fun getClaims(token: String): Claims {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
    }

    fun getClaims(request: HttpServletRequest): Claims? {
        val token = getToken(request) ?: return null
        return getClaims(token)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val USER_NAME = "userName"
    }
}
