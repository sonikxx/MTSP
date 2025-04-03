package rpsLimiter.service

import org.springframework.stereotype.Service
import rpsLimiter.config.RpsLimiterProperties
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


@Service
class RpsLimiterService(
    private val rpsLimiterProperties: RpsLimiterProperties
) {
    private val requestCounters = ConcurrentHashMap<String, AtomicInteger>()
    private val windowStartTimes = ConcurrentHashMap<String, Long>()

    fun tryConsume(quotaName: String): Boolean {
        val now = System.currentTimeMillis()
        val windowStart = windowStartTimes.computeIfAbsent(quotaName) { now }

        if (now - windowStart >= rpsLimiterProperties.windowSize) {
            requestCounters[quotaName] = AtomicInteger(0)
            windowStartTimes[quotaName] = now
        }

        val counter = requestCounters.computeIfAbsent(quotaName) { AtomicInteger(0) }

        return counter.incrementAndGet() <= rpsLimiterProperties.limitPerWindow
    }
}
