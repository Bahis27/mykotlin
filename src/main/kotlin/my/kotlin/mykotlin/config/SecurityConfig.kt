package my.kotlin.mykotlin.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import my.kotlin.mykotlin.model.Role
import my.kotlin.mykotlin.repository.UserRepository
import my.kotlin.mykotlin.web.AuthUser

val PASSWORD_ENCODER: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userRepository: UserRepository,
    private val authenticationEntryPoint: RestAuthenticationEntryPoint

) {
    private val log = KotlinLogging.logger {}

    @Bean
    fun passwordEncoder() = PASSWORD_ENCODER

    @Bean
    fun userDetailsService() = UserDetailsService { email ->
        log.debug { "Authenticating '$email'" }
        AuthUser(
            userRepository.findByEmailIgnoreCase(email) ?: throw UsernameNotFoundException("User '$email' was not found")
        )
    }

    //  https://stackoverflow.com/a/61147599/548473
    @Bean
    fun webSecurityCustomizer() =
        WebSecurityCustomizer { web -> web.ignoring().requestMatchers("/", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**") }

    //https://stackoverflow.com/a/76538979/548473
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/api/**")
            .authorizeHttpRequests { ahrc ->
                ahrc.requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name)
                    .requestMatchers(HttpMethod.POST, "/api/profile").anonymous()
                    .requestMatchers("/api/**").authenticated()
            }
            .httpBasic { hbc -> hbc.authenticationEntryPoint(authenticationEntryPoint) }
            .sessionManagement { smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { obj -> obj.disable() }
        return http.build()
    }
}