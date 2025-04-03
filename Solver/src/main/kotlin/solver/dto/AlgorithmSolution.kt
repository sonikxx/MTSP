package solver.dto

data class AlgorithmSolution (
    var cities: List<List<Point>>,
    var numSalesmen: Int,
    var totalDistance: Double,
)