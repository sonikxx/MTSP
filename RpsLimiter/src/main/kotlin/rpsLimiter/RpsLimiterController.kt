package rpsLimiter

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rps")
class RpsLimiterController(private val rateLimiterService: RpsLimiterService) {

    @PostMapping("/limit")
    fun checkAndIncreaseQuota(@RequestParam quota: String): ResponseEntity<String> {
        return if (rateLimiterService.tryConsume(quota)) {
            ResponseEntity.ok("OK")
        } else {
            ResponseEntity.status(499).body("Too Many Requests")
        }
    }
}