package rpsLimiter.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import rpsLimiter.service.RpsLimiterService

@RestController
@RequestMapping("/rps")
class RpsLimiterController(
    private val rateLimiterService: RpsLimiterService
) {

    @GetMapping("/limit")
    fun checkAndIncreaseQuota(@RequestParam quota: String): ResponseEntity<String> =
        when (rateLimiterService.tryConsume(quota)) {
            true -> ResponseEntity.ok("OK")
            false -> ResponseEntity.status(HttpStatus.OK)
                .body("Quota exceeded for: $quota. Try again later.")
        }
}