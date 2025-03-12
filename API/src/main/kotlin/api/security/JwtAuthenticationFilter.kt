package api.security

import io.jsonwebtoken.Claims
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(
    private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.cookies?.firstOrNull { it.name == "JWT" }?.value

        if (token != null && jwtTokenUtil.validateToken(token)) {
            val claims: Claims = jwtTokenUtil.getClaims(token)
            val userId = claims["userId"].toString().toLong()
            val organizationId = claims["organizationId"].toString().toLong()
            val isAdmin = claims["isAdmin"].toString().toBoolean()

            val userDetails = User(userId.toString(), "", listOf())
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, listOf())
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}
