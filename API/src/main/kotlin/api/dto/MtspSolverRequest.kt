package api.dto

data class MtspSolverRequest(
    val requestId: String,
    val userId: Long,
    val cities: List<Point>,
    val numSalesmen: Long,
    val algorithm: String,
    val algorithmParams: Map<String, Any>
)
