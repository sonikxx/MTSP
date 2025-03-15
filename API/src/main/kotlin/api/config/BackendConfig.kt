package api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "backend")
class BackendConfig {
    lateinit var rps: RpsConfig
    lateinit var mtsp: MTspConfig
}

class RpsConfig {
    lateinit var host: String
    var port: Int = 0
}

class MTspConfig {
    lateinit var host: String
    var port: Int = 0
}
