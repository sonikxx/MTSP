package api.security

import api.rps.RateLimiterFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    @Autowired private val rateLimiterFilter: RateLimiterFilter,
    @Autowired private val jwtInterceptor: JwtInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimiterFilter)
         .addPathPatterns("/**")


        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/protected/**", "/organization/**", "/main/**")
    }
}
