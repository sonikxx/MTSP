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

    fun bruteForceTsp(requestId: String, cities: List<Point>): List<Point> {
        if (cities.isEmpty()) {
            logger.warn { "Empty list of cities" }
            return emptyList()
        }
        logger.info { "$requestId: Start brute force algorithm" }

        val permutations = cities.permutations()
        var shortestRoute = emptyList<Point>()
        var minDistance = Double.MAX_VALUE

        for (perm in permutations) {
            val distance = calculateTotalDistance(perm)
            if (distance < minDistance) {
                minDistance = distance
                shortestRoute = perm
            }
        }

        logger.info { "$requestId: End brute force algorithm" }
        return shortestRoute
    }

    private fun calculateTotalDistance(route: List<Point>): Double {
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += distance(route[i], route[i + 1])
        }
        // Add the distance from the last city back to the first city
        totalDistance += distance(route.last(), route.first())
        return totalDistance
    }

    private fun distance(a: Point, b: Point): Double =
        sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))

    // Extension function to generate all permutations of a list
    private fun <T> List<T>.permutations(): List<List<T>> {
        if (this.isEmpty()) return listOf(emptyList())
        val element = this[0]
        val rest = this.drop(1)
        val perms = rest.permutations()
        val result = mutableListOf<List<T>>()
        for (perm in perms) {
            for (i in 0..perm.size) {
                val newPerm = perm.toMutableList()
                newPerm.add(i, element)
                result.add(newPerm)
            }
        }
        return result
    }
}
