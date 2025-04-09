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

    override fun solve(points: List<Point>, distances: Array<Array<Double>>, numSalesmen: Int) : Flow<Pair<SolutionStatus, AlgorithmSolution>> = flow {
        logger.info { "Start solving with $name algorithm" }
        if (numSalesmen <= 0 || points.isEmpty()) return@flow

        val allPermutations = points.permutations()
        val bestResult = AlgorithmSolution(emptyList(), numSalesmen, Double.MAX_VALUE)
        for (perm in allPermutations) {
            val allDistributions = distributeAmongSalesmen(numSalesmen, perm)
            for (solution in allDistributions) {
                val totalDistance = calculateTotalDistance(distances, solution)
                if (totalDistance < bestResult.totalDistance) {
                    bestResult.totalDistance = totalDistance
                    bestResult.cities = solution
                    logger.info { "New best result: $bestResult" }
                    emit(Pair(SolutionStatus.INTERMEDIATE, bestResult))
                }
            }
        }
        emit(Pair(SolutionStatus.SOLVED, bestResult))
        logger.info { "End solving with $name algorithm" }
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