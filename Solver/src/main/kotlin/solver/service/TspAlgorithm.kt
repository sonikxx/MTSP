package solver.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.dto.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import solver.dto.AlgorithmSolution
import solver.dto.SolutionStatus
import kotlin.math.min
import kotlin.math.sqrt


@Service
class TspAlgorithm {

    fun solveMtsp(cities: List<Point>, numSalesmen: Int) : Flow<Pair<SolutionStatus, AlgorithmSolution>> = flow {
        logger.info { "Start MTSP algorithm" }
        if (numSalesmen <= 0 || cities.isEmpty()) return@flow

        val allPermutations = cities.permutations()
        val bestResult = AlgorithmSolution(emptyList(), -1, Double.MAX_VALUE)
        for (perm in allPermutations) {
            val allDistributions = distributeAmongSalesmen(numSalesmen, perm)
            for (solution in allDistributions) {
                val totalDistance = calculateTotalDistance1(solution)
                if (totalDistance < bestResult.totalDistance) {
                    bestResult.totalDistance = totalDistance
                    bestResult.cities = solution
                    logger.info { "New best result: $bestResult" }
                    emit(Pair(SolutionStatus.INTERMEDIATE, bestResult))
                }
            }
        }
        emit(Pair(SolutionStatus.SOLVED, bestResult))
        logger.info { "End MTSP algorithm" }
    }

    private fun distributeAmongSalesmen(salesmen: Int, points: List<Point>): Sequence<List<List<Point>>> = sequence {
        val maxPartitionSize = points.size / salesmen
        for (partition in generatePartitions(points, salesmen, maxPartitionSize)) {
            yield(partition)
        }
    }

    private fun generatePartitions(points: List<Point>, salesmen: Int, maxSize: Int): Sequence<List<List<Point>>> = sequence {
        if (salesmen == 1) {
            yield(listOf(points))
        } else {
            val maxIndex = min(points.size, maxSize)
            for (i in 1..maxIndex) {
                val firstPartition = points.take(i)
                val remaining = points.drop(i)
                for (subPartition in generatePartitions(remaining, salesmen - 1, maxSize)) {
                    yield(listOf(firstPartition) + subPartition)
                }
            }
        }
    }

    private fun calculateTotalDistance1(routes: List<List<Point>>): Double {
        var totalDistance = 0.0
        for (route in routes) {
            totalDistance += calculateTotalDistance2(route)
        }
        return totalDistance
    }

    private fun calculateTotalDistance2(route: List<Point>): Double {
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += distance(route[i], route[i + 1])
        }
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

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
