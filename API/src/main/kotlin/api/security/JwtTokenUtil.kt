package api.security
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil {

    private val expirationTime = 3600000 // 1 час
    private val secretKey = "MySuperSecretKeyForJWTWhichShouldBeLongEnough"

    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateToken(userId: Long, organizationId: Long, isAdmin: Boolean): String {
        return Jwts.builder()
            .claims(
                mutableMapOf(
                    "userId" to userId,
                    "organizationId" to organizationId,
                    "isAdmin" to isAdmin
                )
            )
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationTime))
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

    fun getClaims(token: String): Claims {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
    }
}
