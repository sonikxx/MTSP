package solver.algorithm

import kotlinx.coroutines.flow.Flow
import solver.dto.AlgorithmSolution
import solver.dto.Point
import solver.dto.SolutionStatus
import kotlin.math.min

abstract class MtspAlgorithm {

    abstract val name: String

    abstract fun solve(
        points: List<Point>,
        distances: Array<Array<Double>>,
        numSalesmen: Int
    ): Flow<Pair<SolutionStatus, AlgorithmSolution>>

    protected fun distance(distances: Array<Array<Double>>, a: Point, b: Point): Double =
        distances[a.id][b.id]

    protected fun calculateRouteDistance(distances: Array<Array<Double>>, route: List<Point>): Double =
        route.zipWithNext().sumOf { (a, b) -> distance(distances, a, b) }

    protected fun calculateTotalDistance(distances: Array<Array<Double>>, routes: List<List<Point>>): Double =
        routes.sumOf { calculateRouteDistance(distances, it) }

    protected fun distributeAmongSalesmen(salesmen: Int, points: List<Point>): Sequence<List<List<Point>>> = sequence {
        val maxPartitionSize = points.size / salesmen
        for (partition in generatePartitions(points, salesmen, maxPartitionSize)) {
            yield(partition)
        }
    }

    protected fun generatePartitions(points: List<Point>, salesmen: Int, maxSize: Int): Sequence<List<List<Point>>> = sequence {
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

}
