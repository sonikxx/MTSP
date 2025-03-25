package solver

import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.dto.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.min
import kotlin.math.sqrt


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

    fun solveMtsp(cities: List<Point>, numSalesmen: Int) : Flow<Map<Int, List<Point>>> = flow {
        logger.info { "Start MTSP algorithm" }
        if (numSalesmen <= 0 || cities.isEmpty()) return@flow

        val allPermutations = cities.permutations()

        for (perm in allPermutations) {
            val allDistributions = distributeAmongSalesmen(numSalesmen, perm)
            for (solution in allDistributions) {
                emit(solution)
            }
        }
        logger.info { "End MTSP algorithm" }
    }

    private fun distributeAmongSalesmen(salesmen: Int, points: List<Point>): Sequence<Map<Int, List<Point>>> = sequence {
        val maxPartitionSize = points.size / salesmen
        for (partition in generatePartitions(points, salesmen, maxPartitionSize)) {
            yield(partition)
        }
    }

    private fun generatePartitions(points: List<Point>, salesmen: Int, maxSize: Int): Sequence<Map<Int, List<Point>>> = sequence {
        if (salesmen == 1) {
            yield(mapOf(0 to points))
        } else {
            val maxIndex = min(points.size, maxSize)
            for (i in 1..maxIndex) {
                val firstPartition = points.take(i)
                val remaining = points.drop(i)
                for (subPartition in generatePartitions(remaining, salesmen - 1, maxSize)) {
                    yield(mapOf(0 to firstPartition) + subPartition.mapKeys { it.key + 1 })
                }
            }
        }
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
    private fun<T> List<T>.permutations(): Sequence<List<T>> = sequence {
        if (isEmpty()) yield(emptyList())
        else {
            for (i in indices) {
                val elem = this@permutations[i]
                val rest = this@permutations - elem
                for (perm in rest.permutations()) {
                    yield(listOf(elem) + perm)
                }
            }
        }
    }
}
