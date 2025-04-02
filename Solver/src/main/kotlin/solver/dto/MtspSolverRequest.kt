package solver.dto

data class MtspSolverRequest(
    val requestId: String,
    val cities: List<City>,
    val numSalesmen: Int
)