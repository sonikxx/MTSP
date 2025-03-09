package Solver

import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import kotlin.math.sqrt
data class TspRequestDto(val cities: List<Point>)
data class TspResponseDto(val route: List<Point>)
data class Point(val x: Double, val y: Double)

@Service
class TspAlgorithm {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun nearestNeighborTsp(requestId: String, cities: List<Point>): List<Point> {
        if (cities.isEmpty()) {
            logger.warn { "Empty list of cities" }
            return emptyList()
        }
        logger.info { "$requestId: Start nearest neighbor algorithm" }

        val visited = mutableSetOf<Point>()
        val route = mutableListOf(cities.first())
        visited.add(cities.first())

        while (visited.size < cities.size) {
            logger.info { "$requestId: Going to sleep for a 5 seconds" }
            Thread.sleep(2000) // TODO: remove sleep. For testing only
            val last = route.last()
            val next = cities.filter { it !in visited }.minByOrNull { distance(last, it) } ?: break
            visited.add(next)
            route.add(next)
        }
        logger.info { "$requestId: End nearest neighbor algorithm" }
        return route
    }
    private fun distance(a: Point, b: Point): Double = sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
}