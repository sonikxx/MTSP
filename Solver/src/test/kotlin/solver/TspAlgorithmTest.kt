import solver.SolverApplication
import solver.TspAlgorithm
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import solver.dto.Point


@SpringBootTest(classes = [SolverApplication::class])
class TspAlgorithmTest {

    @Test
    fun `test brute force with four cities`() {
        val cities = listOf(
            Point(0.0, 0.0),
            Point(1.0, 0.0),
            Point(1.0, 1.0),
            Point(0.0, 1.0)
        )
        val tspAlgorithm = TspAlgorithm()
        val result = tspAlgorithm.bruteForceTsp("testRequest", cities)

        assertNotNull(result)
        Assertions.assertEquals(4, result.size)

        val expectedRoute = listOf(
            Point(0.0, 0.0),
            Point(1.0, 0.0),
            Point(1.0, 1.0),
            Point(0.0, 1.0)
        )
        Assertions.assertEquals(expectedRoute, result)
    }
}