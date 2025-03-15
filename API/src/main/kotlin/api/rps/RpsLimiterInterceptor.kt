package api.rps

import api.config.BackendConfig
import api.security.JwtTokenUtil
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.http.HttpStatus

@Component
class RpsLimiterInterceptor(
    private val restTemplate: RestTemplate,
    private val jwtUtil: JwtTokenUtil,
    private val backendConfig: BackendConfig
) : HandlerInterceptor {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val quotaName = getQuotaName(request)
        try {
            val limiterResponse = restTemplate.getForEntity(
                "http://${backendConfig.rps.host}:${backendConfig.rps.port}/rps/limit?quota=$quotaName",
                String::class.java
            )
            if (!limiterResponse.statusCode.is2xxSuccessful) {
               logger.error { "RPS limiter returned ${limiterResponse.statusCode}" }
                return true
            }
            if (limiterResponse.body?.contains("Quota exceeded") == true) {
                logger.info { "Quota exceeded for $quotaName" }
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded")
                return false
            }
        } catch (e: ResourceAccessException) {
            logger.error { "RPS Limiter unavailable: ${e.message}" }
        } catch (e: RestClientException) {
            logger.error { "RPS Limiter unavailable: ${e.message}" }
        }
        return true
    }

    private fun getQuotaName(request: HttpServletRequest): String {
        return jwtUtil.getClaims(request)?.get("organizationId")?.toString() ?: "unknown"
    }
}
