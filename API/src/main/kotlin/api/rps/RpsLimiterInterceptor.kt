package api.rps

import api.security.JwtTokenUtil
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RpsLimiterInterceptor(
    private val restTemplate: RestTemplate,
    private val jwtUtil: JwtTokenUtil
) : HandlerInterceptor {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val quotaName = determineQuota(request)
        return try {
            val limiterResponse = restTemplate.getForEntity("http://localhost:6969/rps/limit?quota=$quotaName", String::class.java)
            limiterResponse.statusCode.is2xxSuccessful
        } catch (e: RestClientException) {
            logger.error { "RPS Limiter unavailable: ${e.message}" }
            true
        }
    }

    private fun determineQuota(request: HttpServletRequest): String {
        return jwtUtil.getClaims(request)?.get("organizationId").toString() // TODO: use unknown organization if no organizationId in token
    }
}
