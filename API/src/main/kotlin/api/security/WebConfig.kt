package api.security

import api.rps.RpsLimiterInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    @Autowired private val rateLimiterFilter: RpsLimiterInterceptor,
    @Autowired private val jwtInterceptor: JwtInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/protected/**", "/organization/**", "/main/**")

        registry.addInterceptor(rateLimiterFilter)
            .addPathPatterns("/**")
    }
}
