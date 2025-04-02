package api.dto

data class MtspSolverRequest(
    val requestId: String,
    val cities: List<PointDto>,
    val numSalesmen: Int
)