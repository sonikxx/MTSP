package api.dto

data class MtspSolverRequest(
    val requestId: String,
    val cities: List<Point>,
    val numSalesmen: Int
)
