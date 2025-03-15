package rpsLimiter

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


@Service
class RpsLimiterService {
    private val limitPerWindow = 5
    private val requestCounters = ConcurrentHashMap<String, AtomicInteger>()
    private val windowStartTimes = ConcurrentHashMap<String, Long>()
    private val windowSize = 10000L

    fun tryConsume(quotaName: String): Boolean {
        val now = System.currentTimeMillis()
        val windowStart = windowStartTimes.computeIfAbsent(quotaName) { now }

        if (now - windowStart >= windowSize) {
            requestCounters[quotaName] = AtomicInteger(0)
            windowStartTimes[quotaName] = now
        }

        val counter = requestCounters.computeIfAbsent(quotaName) { AtomicInteger(0) }

        return counter.incrementAndGet() <= limitPerWindow
    }
}
