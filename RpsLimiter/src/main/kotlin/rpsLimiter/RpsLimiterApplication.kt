package rpsLimiter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RpsLimiterApplication

fun main(args: Array<String>) {
	runApplication<RpsLimiterApplication>(*args)
}
