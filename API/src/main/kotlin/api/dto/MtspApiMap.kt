package api.dto

data class MtspApiMap (
    val name: String,
    val cities: List<City>,
    val distances: List<List<Double>>,
    val isPublic: Boolean = false
)