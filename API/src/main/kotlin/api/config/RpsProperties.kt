package api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "backend.rps")
data class RpsProperties(
    val host: String,
    val port: Int = 0
)
