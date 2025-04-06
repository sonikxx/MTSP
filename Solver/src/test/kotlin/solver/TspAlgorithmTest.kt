import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import solver.algorithm.BruteForceAlgorithm
import solver.algorithm.GeneticAlgorithm
import solver.algorithm.MtspAlgorithmFactory
import solver.dto.Point
import solver.dto.SolutionStatus


class MtspAlgorithmTest {

    private val mtspAlgorithmFactory = MtspAlgorithmFactory(listOf(BruteForceAlgorithm(), GeneticAlgorithm()))

    @Test
    fun `test brute force with five cities`(): Unit = runBlocking {
        // Given: A small set of 5 cities
        val cities = listOf(
            Point(0, 0.0, 1.0),
            Point(1, 1.0, 0.0),
            Point(2, 10.0, 10.0),
            Point(3, 9.0, 10.0),
            Point(4, 10.0, 9.0),
        )
        val numSalesmen = 2

        val tspAlgorithm = mtspAlgorithmFactory.get("bruteForce")!!
        val solutions = tspAlgorithm.solve(cities, numSalesmen).toList()

        assertThat(solutions.first().first).isEqualTo(SolutionStatus.INTERMEDIATE)
        assertThat(solutions.last().first).isEqualTo(SolutionStatus.SOLVED)
    }
}