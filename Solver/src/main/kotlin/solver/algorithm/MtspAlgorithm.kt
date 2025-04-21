package solver.algorithm

import kotlinx.coroutines.flow.Flow
import solver.dto.AlgorithmSolution
import solver.dto.Point
import solver.dto.SolutionStatus

abstract class MtspAlgorithm {

    abstract val name: String

    abstract fun solve(
        inputPoints: List<Point>,
        distances: Array<Array<Double>>,
        numSalesmen: Int,
        algorithmParams: Map<String, String> = emptyMap()
    ): Flow<Pair<SolutionStatus, AlgorithmSolution>>

    private fun distance(distances: Array<Array<Double>>, aId: Int, bId: Int): Double =
        distances[aId][bId]

    protected fun calculateRouteDistance(distances: Array<Array<Double>>, route: List<Point>): Double =
        route.zipWithNext().sumOf { (a, b) -> distance(distances, a.id, b.id) } + distance(distances, 0, route.first().id) + distance(distances, 0, route.last().id)

    protected fun calculateTotalDistance(distances: Array<Array<Double>>, routes: List<List<Point>>): Double =
        routes.sumOf { calculateRouteDistance(distances, it) }

    protected fun distributeAmongSalesmen(salesmen: Int, points: List<Point>): Sequence<List<List<Point>>> = sequence {
        for (partition in generatePartitions(points, salesmen)) {
            yield(partition)
        }
    }

    private fun generatePartitions(points: List<Point>, salesmen: Int): Sequence<List<List<Point>>> = sequence {
        if (salesmen == 1) {
            if (points.isNotEmpty()) {
                yield(listOf(points))
            }
        } else {
            val maxIndex = points.size - (salesmen - 1)
            for (i in 1..maxIndex) {
                val firstPartition = points.take(i)
                val remaining = points.drop(i)
                for (subPartition in generatePartitions(remaining, salesmen - 1)) {
                    yield(listOf(firstPartition) + subPartition)
                }
            }
        }
    }

}
