package api.dto

data class MtspApiRequest(
    val mapId: Long,
    val salesmanNumber: Long,
    val algorithm: String = "bruteForce",
    val algorithmParams: Map<String, Any> = emptyMap()
)
