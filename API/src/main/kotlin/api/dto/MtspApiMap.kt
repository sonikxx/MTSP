package api.dto

data class MtspApiMap(
    val id: Long = -1,
    val name: String,
    val cities: List<City>,
    val distances: List<List<Double>>,
    val isPublic: Boolean = false,
    val ownerName: String = ""
)
