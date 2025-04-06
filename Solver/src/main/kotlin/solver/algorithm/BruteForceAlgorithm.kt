package solver.algorithm


import solver.dto.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.dto.AlgorithmSolution
import solver.dto.SolutionStatus

@Service
class BruteForceAlgorithm : MtspAlgorithm() {
    override val name: String = "bruteForce"

    override fun solve(cities: List<Point>, numSalesmen: Int) : Flow<Pair<SolutionStatus, AlgorithmSolution>> = flow {
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