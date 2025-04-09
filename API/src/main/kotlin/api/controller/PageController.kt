package api.controller

import api.service.PageService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class PageController(
    private val pageService: PageService
) {

    @GetMapping
    fun index(): ResponseEntity<Resource> {
        val file = pageService.index()

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }

    @GetMapping("login")
    fun login(): ResponseEntity<Resource> {
        val file = pageService.login()

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }

    @GetMapping("main/{organizationId}")
    fun mainPage(@PathVariable organizationId: Long): ResponseEntity<Resource> {
        val file = pageService.mainPage(organizationId)

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }
}

// TODO: use Thymeleaf
// @Controller
// class MainPageController {
//
//    @GetMapping("main/{organizationId}")
//    fun mainPage(@PathVariable organizationId: String, model: Model): String {
//        model.addAttribute("organizationName", "Your Organization Name")
//        return "mainPage" // this refers to src/main/resources/templates/mainPage.html
//    }
// }
