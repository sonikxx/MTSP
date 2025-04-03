package rpsLimiter.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rps")
data class RpsLimiterProperties (
    val limitPerWindow: Int,
    val windowSize: Long
)
