package solver.algorithm

import org.springframework.stereotype.Service
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import solver.dto.AlgorithmSolution
import solver.dto.Point
import solver.dto.SolutionStatus
import kotlinx.coroutines.flow.flow
import kotlin.random.Random


@Service
class GeneticAlgorithm : MtspAlgorithm() {

    override val name: String = "genetic"

    override fun solve(
        cities: List<Point>,
        distances: Array<Array<Double>>,
        numSalesmen: Int
    ): Flow<Pair<SolutionStatus, AlgorithmSolution>> = flow {
        logger.info { "Start genetic algorithm" }

        val populationSize = 1000
        val generations = 5000
        val mutationRate = 0.1
        val updateInterval = 50

        var population = generateInitialPopulation(populationSize, cities, numSalesmen)
        var best = population.minByOrNull { calculateTotalDistance(distances, it) }!!
        var currentBestGenerationNumber = 0

        emit(SolutionStatus.INTERMEDIATE to AlgorithmSolution(best, -1, calculateTotalDistance(distances, best)))

        repeat(generations) { gen ->
//            logger.info { "Generation $gen" }
            population = evolvePopulation(distances, population, mutationRate)
            val currentBest = population.minByOrNull { calculateTotalDistance(distances, it) }!!
            if (calculateTotalDistance(distances, currentBest) < calculateTotalDistance(distances, best)) {
                best = currentBest
                if (currentBestGenerationNumber % updateInterval == 0) {
                    logger.info { "Generation $gen" }
                    emit(SolutionStatus.INTERMEDIATE to AlgorithmSolution(best, gen, calculateTotalDistance(distances, best)))
                }
                currentBestGenerationNumber++
            }
        }

        emit(SolutionStatus.SOLVED to AlgorithmSolution(best, generations, calculateTotalDistance(distances, best)))
        logger.info { "End genetic algorithm" }
    }

    private fun generateInitialPopulation(
        size: Int,
        cities: List<Point>,
        numSalesmen: Int
    ): List<List<List<Point>>> {
        return List(size) {
            val shuffled = cities.shuffled()
            distributeAmongSalesmen(numSalesmen, shuffled).first()
        }
    }

    private fun evolvePopulation(
        distances: Array<Array<Double>>,
        population: List<List<List<Point>>>,
        mutationRate: Double
    ): List<List<List<Point>>> {
        val sorted = population.sortedBy { calculateTotalDistance(distances, it) }
        val survivors = sorted.take(population.size / 2)
        val offspring = mutableListOf<List<List<Point>>>()

        while (offspring.size < population.size / 2) {
            val parent1 = survivors.random()
            val parent2 = survivors.random()
            val child = crossover(parent1, parent2)
            offspring += mutate(child, mutationRate)
        }

        return survivors + offspring
    }

    private fun crossover(
        parent1: List<List<Point>>,
        parent2: List<List<Point>>
    ): List<List<Point>> {
        val flat1 = parent1.flatten().toMutableList()
        val flat2 = parent2.flatten().toSet()
        val result = mutableListOf<List<Point>>()
        val portionSize = flat1.size / parent1.size

        for (i in 0 until parent1.size) {
            val part = flat1.filter { it in flat2 }.drop(i * portionSize).take(portionSize)
            result += part
        }
        return result
    }

    private fun mutate(solution: List<List<Point>>, rate: Double): List<List<Point>> {
        return solution.map { route ->
            if (Random.nextDouble() < rate) route.shuffled() else route
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}