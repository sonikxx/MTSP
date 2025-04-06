package api.dto

data class MtspSolverRequest(
    val requestId: String,
    val userId: Long,
    val cities: List<Point>,
    val numSalesmen: Int,
    val algorithm: String = "bruteForce",
    val algorithmParams: Map<String, Any> = emptyMap()
)
