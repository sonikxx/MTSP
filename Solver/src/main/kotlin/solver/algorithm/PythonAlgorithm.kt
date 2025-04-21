package solver.algorithm

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.springframework.stereotype.Service
import solver.dto.AlgorithmSolution
import solver.dto.Point
import solver.dto.SolutionStatus

@Service
class PythonAlgorithm: MtspAlgorithm() {

    override val name: String = "python"

    private val objectMapper = jacksonObjectMapper()

    override fun solve(
        inputPoints: List<Point>,
        distances: Array<Array<Double>>,
        numSalesmen: Int,
        algorithmParams: Map<String, String>
    ): Flow<Pair<SolutionStatus, AlgorithmSolution>> = flow {
        val input = mapOf(
            "algorithm_name" to "SA",
            "params" to mapOf(
                "distance_weight" to (algorithmParams["distance_weight"] ?: "1").toInt(),
                "balance_weight" to (algorithmParams["balance_weight"] ?: "20").toInt(),
            ),
            "number_of_salesmen" to numSalesmen,
            "distance_matrix" to distances.map { it.toList() }
        )

        val process = ProcessBuilder("python3", "scripts/main.py")
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()

        // Отправляем JSON во входной поток python-процесса
        process.outputStream.bufferedWriter().use { writer ->
            objectMapper.writeValue(writer, input)
        }

        // Читаем JSON-ответы построчно и emit-им
        process.inputStream.bufferedReader().useLines { lines ->
            for (line in lines) {
                if (line.isBlank()) continue

                val jsonNode = objectMapper.readTree(line)
                val status = when (jsonNode["status"].asText()) {
                    "intermediate" -> SolutionStatus.INTERMEDIATE
                    else -> SolutionStatus.SOLVED
                }

                val solutionNode = jsonNode["solution"]
                val citiesRaw: List<List<Int>> =
                    objectMapper.convertValue(solutionNode["cities"], object : TypeReference<List<List<Int>>>() {})

                val cities: List<List<Point>> = citiesRaw.map { route ->
                    route.map { index -> Point(id = index - 1) }
                }

                val solution = AlgorithmSolution(
                    cities = cities,
                    numSalesmen = solutionNode["numSalesmen"].asInt(),
                    totalDistance = solutionNode["totalDistance"].asDouble()
                )

                logger.info { "Emitting solution: $solution" }


                emit(status to solution)
            }
        }

        process.waitFor()
    }


    companion object {
        private val logger = KotlinLogging.logger {}
    }
}