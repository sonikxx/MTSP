package Api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HelloController(private val grpcClientService: GrpcClientService) {

    @GetMapping("/hello")
    fun sayHello(): String {
        return grpcClientService.getEchoMessage("Hello from API! Cudaaaaa")
    }
}