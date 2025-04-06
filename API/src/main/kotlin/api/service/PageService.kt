package api.service

import mu.KotlinLogging
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class PageService {

    fun index(): ClassPathResource =
        ClassPathResource("static/pages/index.html").apply {
            logger.info { "request index page" }
        }

    fun login(): ClassPathResource =
        ClassPathResource("static/pages/login.html").apply {
            logger.info { "request login page" }
        }

    fun mainPage(organizationId: Long): ClassPathResource =
        ClassPathResource("static/pages/main.html").apply {
            logger.info { "request main page for organization: $organizationId" }
        }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
