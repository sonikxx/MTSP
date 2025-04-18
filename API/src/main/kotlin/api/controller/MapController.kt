package api.controller

import api.dto.MtspApiMap
import api.dto.MtspEdge
import api.dto.MtspMap
import api.repository.MtspMapRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestAttribute

@RestController
@RequestMapping("/protected/v1")
class MapController(
    private val mtspMapRepository: MtspMapRepository
) {
    @PostMapping("save/map")
    fun save(
        @RequestBody request: MtspApiMap,
        @RequestAttribute(name = "userId") userId: Long,
    ): ResponseEntity<Map<String, Long>> {
         val map = MtspMap(
            userId = userId,
            name = request.name,
            isPublic = request.isPublic,
            points = request.cities,
        )

        request.distances.forEachIndexed { fromNode, edge ->
            edge.forEachIndexed { toNode, distance ->
                map.addEdge(
                    MtspEdge(
                        map = map,
                        fromNode = fromNode,
                        toNode = toNode,
                        distance = distance
                    )
                )
            }
        }
        val saved = mtspMapRepository.save(map)
        return ResponseEntity.ok(
            mapOf(
                "mapId" to saved.id
            )
        )
    }

    @GetMapping("map/{mapId}")
    fun getMap(
        @PathVariable mapId: Long,
        @RequestAttribute(name = "userId") userId: Long,
        @RequestAttribute(name = "userName") username: String,
    ): ResponseEntity<MtspApiMap> {
        val map = mtspMapRepository.findByIdAndUserId(mapId, userId)
            ?: return ResponseEntity.notFound().build()

        val distances = Array(map.points.size) { Array(map.points.size) { Double.POSITIVE_INFINITY } }

        for (edge in map.edges) {
            distances[edge.fromNode][edge.toNode] = edge.distance
        }

        return ResponseEntity.ok(
            MtspApiMap(
                name = map.name,
                cities = map.points,
                distances = distances.map { it.toList() },
                isPublic = map.isPublic,
                ownerName = username
            )
        )
    }

    @GetMapping("maps")
    fun getAvailableMaps(
        @RequestAttribute(name = "userId") userId: Long
    ): ResponseEntity<List<MtspApiMap>> {
        val maps = mtspMapRepository.findAllByUserIdOrIsPublicTrueDistinct(userId)

        return ResponseEntity.ok(
            maps.map { mtspMap ->
                MtspApiMap(
                    id = mtspMap.id,
                    name = mtspMap.name,
                    cities = emptyList(),
                    distances = emptyList(),
                    isPublic = mtspMap.isPublic
                )
            }
        )
    }

    @GetMapping("info")
    fun getInfo(
        @RequestAttribute(name = "userId") userId: Long,
        @RequestAttribute(name = "userName") username: String
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(
            mapOf(
                "userName" to username,
                "userId" to userId.toString()
                )
            )
    }
}