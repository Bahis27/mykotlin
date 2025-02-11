package my.kotlin.mykotlin.config

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule
import io.github.oshai.kotlinlogging.KotlinLogging
import org.h2.tools.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.ProblemDetail
import my.kotlin.mykotlin.util.JsonUtil

@Configuration
@EnableCaching
class AppConfig {
    private val log = KotlinLogging.logger {}

    @Profile("!test")
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun h2Server(): Server {
        log.info { "Start H2 TCP server" }
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092")
    }

    //   https://stackoverflow.com/a/74630129/548473
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.ANY)
    internal interface MixIn {
        @get:JsonAnyGetter
        val properties: Map<String, Any>
    }

    @Autowired
    fun configureAndStoreObjectMapper(objectMapper: ObjectMapper) {
        objectMapper.registerModule(Hibernate5JakartaModule())
        // ErrorHandling: https://stackoverflow.com/questions/7421474/548473
        objectMapper.addMixIn(ProblemDetail::class.java, MixIn::class.java)
        JsonUtil.setMapper(objectMapper)
    }
}
