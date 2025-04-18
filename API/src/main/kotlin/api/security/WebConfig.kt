package api.security

import api.rps.RpsLimiterInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val rpsLimiterInterceptor: RpsLimiterInterceptor,
    private val jwtInterceptor: JwtInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/protected/**", "/organization/**", "/main/**", "/create/**")

        registry.addInterceptor(rpsLimiterInterceptor)
            .addPathPatterns("/**")
    }
}
