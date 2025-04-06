package solver.dto

data class MtspSolverRequest(
    val requestId: String,
    val userId: Long,
    val cities: List<City>,
    val numSalesmen: Int
)