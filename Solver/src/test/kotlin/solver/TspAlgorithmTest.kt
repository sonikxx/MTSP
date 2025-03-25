import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import solver.SolverApplication
import solver.TspAlgorithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import solver.dto.Point


@SpringBootTest(classes = [SolverApplication::class])
class TspAlgorithmTest {

    private val tspAlgorithm = TspAlgorithm()

    @Test
    fun `test brute force with four cities`() = runBlocking {
        // Given: A small set of 4 cities
        val cities = listOf(
            Point(0, 0.0, 0.0),
            Point(1, 1.0, 0.0),
            Point(2, 1.0, 1.0),
            Point(3, 0.0, 1.0)
        )
        val numSalesmen = 2

        val solutions = tspAlgorithm.solveMtsp(cities, numSalesmen).toList()

        assert(solutions.isNotEmpty()) { "No solutions were found!" }

        val firstSolution = solutions.first()
        assertEquals(numSalesmen, firstSolution.size, "Solution should contain routes for each salesman")

        val visitedCities = firstSolution.values.flatten().map { it.id }.toSet()
        assertEquals(cities.map { it.id }.toSet(), visitedCities, "All cities must be visited exactly once")
    }
}