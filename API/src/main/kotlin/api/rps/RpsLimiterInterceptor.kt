package api.rps

import api.config.BackendConfig
import api.security.JwtTokenUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RpsLimiterInterceptor(
    private val restTemplate: RestTemplate,
    private val jwtUtil: JwtTokenUtil,
    private val backendConfig: BackendConfig
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val quotaName = getQuotaName(request)

        return try {
            val limiterResponse = restTemplate.getForEntity(
                "http://${backendConfig.rps.host}:${backendConfig.rps.port}/rps/limit?quota=$quotaName",
                String::class.java
            )

            when {
                !limiterResponse.statusCode.is2xxSuccessful -> {
                    logger.error { "RPS limiter returned ${limiterResponse.statusCode}" }
                    true
                }
                limiterResponse.body?.contains("Quota exceeded") == true -> {
                    logger.info { "Quota exceeded for $quotaName" }
                    response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded")
                    false
                }
                else -> true
            }
        } catch (e: Exception) {
            logger.error(e.message) { "RPS Limiter unavailable" }
            true
        }
    }

    private fun getQuotaName(request: HttpServletRequest): String {
        return jwtUtil.getClaims(request)?.get("organizationId")?.toString() ?: "unknown"
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
