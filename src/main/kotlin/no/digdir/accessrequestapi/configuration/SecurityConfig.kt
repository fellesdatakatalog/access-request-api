package no.digdir.accessrequestapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration
open class SecurityConfig(
    val corsProperties: CorsProperties,
) {
    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { corsConfigurer ->
                corsConfigurer.configurationSource {
                    CorsConfiguration().also {
                        it.allowCredentials = false
                        it.allowedHeaders = listOf("*")
                        it.maxAge = 3600L
                        it.allowedOriginPatterns = corsProperties.originPatterns.toList()
                        it.allowedMethods = listOf("GET", "POST", "OPTIONS")
                    }
                }
            }.csrf {
                it.disable()
            }.sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
        return http.build()
    }
}
