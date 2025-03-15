package rpsLimiter

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rps")
class RpsLimiterController(private val rateLimiterService: RpsLimiterService) {

    @GetMapping("/limit")
    fun checkAndIncreaseQuota(@RequestParam quota: String): ResponseEntity<String> {
        return if (rateLimiterService.tryConsume(quota)) {
            ResponseEntity.ok("OK")
        } else {
            ResponseEntity.ok("Quota exceeded for: $quota. Try again later.")
        }
    }
}