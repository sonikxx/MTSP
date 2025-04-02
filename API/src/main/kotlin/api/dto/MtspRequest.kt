package api.dto

data class MtspRequest(
    val salesmanNumber: Int,
    val cities: List<PointDto>
)
