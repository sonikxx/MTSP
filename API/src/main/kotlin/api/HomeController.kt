package api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

    @GetMapping("/")
    fun home(): String {
        return "forward:index.html"  // Spring Boot будет искать файл /src/main/resources/static/index.html
    }
}
