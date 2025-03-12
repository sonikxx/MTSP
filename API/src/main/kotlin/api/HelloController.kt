package api

import api.dto.TspRequestDto
import api.dto.TspResponseDto
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class HelloController(private val grpcClientService: GrpcClientService) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/solve")
    fun solve(@RequestBody request: TspRequestDto): TspResponseDto {
        logger.info { "Received request!" }
        return grpcClientService.solveTsp(request)
    }



}