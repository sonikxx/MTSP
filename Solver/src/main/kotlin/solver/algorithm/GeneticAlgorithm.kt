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
        points: List<Point>,
        distances: Array<Array<Double>>,
        numSalesmen: Int
    ): Flow<Pair<SolutionStatus, AlgorithmSolution>> = flow {
        logger.info { "Start genetic algorithm" }

        val populationSize = 1000
        val generations = 5000
        val mutationRate = 0.1
        val updateInterval = 50

        var population = generateInitialPopulation(populationSize, points, numSalesmen)
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
        // Flatten the parents
        val flat1 = parent1.flatten()
        val flat2 = parent2.flatten()
        val size = flat1.size

        val child = MutableList<Point?>(size) { null }

        // Step 1: Select a random swath
        val (start, end) = Pair((0 until size).random(), (0 until size).random()).let {
            if (it.first < it.second) it else it.second to it.first
        }

        // Step 2: Copy the swath from flat1 to the child
        for (i in start..end) {
            child[i] = flat1[i]
        }

        // Step 3: Fill remaining positions with flat2's elements
        var currentIndex = (end + 1) % size
        flat2.forEach { point ->
            if (point !in child) {
                child[currentIndex] = point
                currentIndex = (currentIndex + 1) % size
            }
        }

        val result = mutableListOf<List<Point>>()
        val portionSize = size / parent1.size
        for (i in parent1.indices) {
            val part = child.drop(i * portionSize).take(portionSize).map { it!! }
            result.add(part)
        }

        return result
    }

    private fun mutate(solution: List<List<Point>>, rate: Double): List<List<Point>> {
        if (Random.nextDouble() >= rate) return solution

        val result = solution.map { it.toMutableList() }.toMutableList()

        when ((1..3).random()) {
            1 -> {
                val index = result.indices.random()
                result[index] = result[index].shuffled().toMutableList()
            }
            2 -> {
                val fromIndex = result.indices.random()
                val toIndex = result.indices.random()
                if (fromIndex != toIndex && result[fromIndex].isNotEmpty()) {
                    val point = result[fromIndex].removeAt(result[fromIndex].indices.random())
                    result[toIndex].add(point)
                }
            }
            3 -> {
                if (result.size >= 2) {
                    val fromIndex = result.indices.random()
                    val toIndex = result.indices.random()
                    if (fromIndex != toIndex && result[fromIndex].isNotEmpty() && result[toIndex].isNotEmpty()) {
                        val fromPointIndex = result[fromIndex].indices.random()
                        val toPointIndex = result[toIndex].indices.random()
                        val temp = result[fromIndex][fromPointIndex]
                        result[fromIndex][fromPointIndex] = result[toIndex][toPointIndex]
                        result[toIndex][toPointIndex] = temp
                    }
                }
            }
        }

        return result
    }


    companion object {
        private val logger = KotlinLogging.logger {}
    }
}