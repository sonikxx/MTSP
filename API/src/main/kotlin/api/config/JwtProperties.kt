package api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "backend.jwt")
data class JwtProperties(
    val secretKey: String,
    val cookie: CookieProperties
)

data class CookieProperties(
    val name: String,
    val path: String,
    val isHttpOnly: Boolean,
    val secure: Boolean,
    val maxAge: Int
)
