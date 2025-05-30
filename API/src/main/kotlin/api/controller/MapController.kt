package api.controller

import api.dto.MtspApiMap
import api.dto.MtspEdge
import api.dto.MtspMap
import api.repository.MtspMapRepository
import api.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RestController
@RequestMapping("/protected/v1")
class MapController(
    private val mtspMapRepository: MtspMapRepository,
    private val userRepository: UserRepository
) {
    @PostMapping("save/map")
    fun save(
        @RequestBody request: MtspApiMap,
        @RequestAttribute(name = "userId") userId: Long
    ): ResponseEntity<Map<String, Long>> {
        val cityCount = request.cities.size

        if (cityCount < 2 || cityCount > 1000) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cities count must be between 2 and 1000"
            )
        }

        if (request.distances.size != cityCount) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Distances matrix must have the same number of rows as there are cities"
            )
        }

        request.distances.forEachIndexed { rowIndex, row ->
            if (row.size != cityCount) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Distances matrix row $rowIndex must have exactly $cityCount columns"
                )
            }
        }

        val map = MtspMap(
            userId = userId,
            name = request.name,
            isPublic = request.isPublic,
            points = request.cities
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
        @RequestAttribute(name = "userName") myUsername: String
    ): ResponseEntity<MtspApiMap> {
        val map = mtspMapRepository.findAccessibleMapById(mapId, userId)
            ?: return ResponseEntity.notFound().build()

        val distances = Array(map.points.size) { Array(map.points.size) { Double.POSITIVE_INFINITY } }

        for (edge in map.edges) {
            distances[edge.fromNode][edge.toNode] = edge.distance
        }

        val user = userRepository.findById(map.userId)
        var username = "Anonymous"
        if (!user.isEmpty) {
            username = user.get().email
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
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")
            .withZone(ZoneId.of("Europe/Moscow"))

        val userIds = maps.map { it.userId }.toSet()
        val usersById = userRepository.findAllById(userIds).associateBy { it.id }

        return ResponseEntity.ok(
            maps.map { mtspMap ->
                MtspApiMap(
                    id = mtspMap.id,
                    name = mtspMap.name,
                    cities = emptyList(),
                    distances = emptyList(),
                    isPublic = mtspMap.isPublic,
                    ownerName = usersById[mtspMap.userId]?.email?: "Anonymous",
                    creationDate = formatter.format(mtspMap.createdAt),
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
