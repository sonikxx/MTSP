package api.controller

import api.service.PageService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestAttribute
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

    @GetMapping("register")
    fun register(): ResponseEntity<Resource> {
        val file = pageService.register()

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }

    @GetMapping("login")
    fun login(): ResponseEntity<Resource> {
        val file = pageService.login()

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }

    @GetMapping("create")
    fun create(@RequestAttribute(name = "userId") userId: Long): ResponseEntity<Resource> {
        val file = pageService.create()

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }

    @GetMapping("main/{mapId}")
    fun mainPage(@RequestAttribute(name = "userId") userId: Long, @PathVariable mapId: Long): ResponseEntity<Resource> {
        val file = pageService.mainPage(userId, mapId)

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(file)
    }
}
