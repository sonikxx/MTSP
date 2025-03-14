package rpsLimiter

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@RestController
@RequestMapping("/api/rpslimiter")
class RateLimiterController {

    private val requestCountMap = ConcurrentHashMap<String, AtomicInteger>()

    private val maxRequestsPerMinute = 10
    private val resetIntervalMillis = 60 * 1000L // 1 minute

    @PostMapping("/increaseQuota")
    fun increaseQuota(@RequestBody request: RateLimitRequest): ResponseEntity<String> {
        val userKey = getUserKey(request.userId, request.organizationId)

        val counter = requestCountMap.computeIfAbsent(userKey) { AtomicInteger(0) }

        if (counter.get() >= maxRequestsPerMinute) {
            return ResponseEntity("Rate limit exceeded. Try again later.", HttpStatus.TOO_MANY_REQUESTS)
        }

        counter.incrementAndGet()

//        Thread {
//            Thread.sleep(resetIntervalMillis)
//            counter.set(0)
//        }.start()

        return ResponseEntity("Quota increased. You have ${maxRequestsPerMinute - counter.get()} requests left.", HttpStatus.OK)
    }

    @GetMapping("/quota")
    fun getQuota(@RequestParam userId: String, @RequestParam organizationId: String): ResponseEntity<String> {
        val userKey = getUserKey(userId, organizationId)
        val counter = requestCountMap[userKey]

        val remainingQuota = maxRequestsPerMinute - (counter?.get() ?: 0)
        return if (remainingQuota > 0) {
            ResponseEntity("You have $remainingQuota requests left.", HttpStatus.OK)
        } else {
            ResponseEntity("Rate limit exceeded. Try again later.", HttpStatus.TOO_MANY_REQUESTS)
        }
    }

    private fun getUserKey(userId: String, organizationId: String): String {
        return "$organizationId-$userId"
    }
}

data class RateLimitRequest(val userId: String, val organizationId: String)
