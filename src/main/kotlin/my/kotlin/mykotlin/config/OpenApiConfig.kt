package my.kotlin.mykotlin.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration //https://sabljakovich.medium.com/adding-basic-auth-authorization-option-to-openapi-swagger-documentation-java-spring-95abbede27e9
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@OpenAPIDefinition(
    info = Info(
        title = "REST API documentation", version = "1.0",
        description = """
Приложение по <a href='https://javaops.ru/view/topkotlin'>курсу TopKotlin</a>
<p><b>Тестовые креденшелы:</b><br>
- user@yandex.ru / password<br>
- admin@gmail.com / admin<br>
- guest@gmail.com / guest</p>
""",
        contact = Contact(url = "https://javaops.ru/#contacts", name = "Grigory Kislin", email = "admin@javaops.ru")
    ), security = [SecurityRequirement(name = "basicAuth")]
)
class OpenApiConfig {
    @Bean
    fun api(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("REST API")
        .pathsToMatch("/api/**")
        .build()
}
