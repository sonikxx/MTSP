package api

import mu.KotlinLogging
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class PageController {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @GetMapping
    fun index(): ResponseEntity<Resource> {
        logger.info { "request index page" }
        val file = ClassPathResource("static/pages/index.html")
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }

    @GetMapping("login")
    fun login(): ResponseEntity<Resource> {
        logger.info { "request login page" }
        val file = ClassPathResource("static/pages/login.html")
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }

    @GetMapping("main/{organization}")
    fun mainPage(@PathVariable organization: String): ResponseEntity<Resource> {
        logger.info { "request main page for organization: $organization" }
        val file = ClassPathResource("static/pages/main.html")
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
//        return ResponseEntity.ok("Вы на странице организации: $organization")
    }
}

