package api.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor(
    private val jwtUtil: JwtTokenUtil
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val claims = jwtUtil.getClaims(request)
        if (claims == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized: Invalid or missing JWT")
            response.sendRedirect("/login")
            return false
        }

        request.setAttribute(USER_ID, claims[USER_ID].toString().toLong())
        return true
    }

    companion object {
        private const val USER_ID = "userId"
    }
}
