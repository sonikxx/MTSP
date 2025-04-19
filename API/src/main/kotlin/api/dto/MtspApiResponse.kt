package api.dto

data class MtspApiResponse(
    val status: String = "INTERMEDIATE",
    val routes: List<List<City>> = emptyList(),
    val totalCost: Double = 0.0,
    val totalTime: Long = 0,
    val algorithm: String = "",
)
