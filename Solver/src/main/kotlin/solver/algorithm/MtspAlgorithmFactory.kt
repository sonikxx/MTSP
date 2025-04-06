package solver.algorithm

import org.springframework.stereotype.Service

@Service
class MtspAlgorithmFactory(solvers: List<MtspAlgorithm>) {
    private val registry = solvers.associateBy { it.name }

    fun get(name: String): MtspAlgorithm? = registry[name]
}