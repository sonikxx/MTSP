package api.dto

data class MtspResponse(
    val status: String = "INTERMEDIATE",
    val routes: List<List<Point>> = emptyList()
)
