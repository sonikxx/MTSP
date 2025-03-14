package api.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor(private val jwtUtil: JwtTokenUtil) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.cookies?.firstOrNull { it.name == "JWT" }?.value

        if (token.isNullOrBlank() || !jwtUtil.validateToken(token)) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized: Invalid or missing JWT")
            return false
        }

        val claims = jwtUtil.getClaims(token)
        request.setAttribute("userId", claims["userId"].toString().toLong())
        request.setAttribute("organizationId", claims["organizationId"].toString().toLong())
        request.setAttribute("isAdmin", claims["isAdmin"].toString().toBoolean())

        return true
    }
}
