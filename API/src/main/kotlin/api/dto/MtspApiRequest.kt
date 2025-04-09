package api.dto

data class MtspApiRequest(
    val salesmanNumber: Long,
    val cities: List<City>,
    val distances: List<List<Double>>,
    val algorithm: String = "bruteForce",
    val algorithmParams: Map<String, Any> = emptyMap()
)
