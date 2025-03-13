package no.digdir.accessrequestapi.configuration

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfiguration

@Configuration
open class SecurityConfig(private val corsProperties: CorsProperties) {

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { corsConfigurer ->
                corsConfigurer.configurationSource {
                    CorsConfiguration().also {
                        it.allowCredentials = true
                        it.allowedHeaders = listOf("*")
                        it.maxAge = 3600L
                        it.allowedOriginPatterns = corsProperties.originPatterns.toList()
                        it.allowedMethods = listOf("GET", "POST", "OPTIONS")
                    }
                }
            }
            .csrf {
                it.requireCsrfProtectionMatcher(CsrfRequiredMatcher(corsProperties.originPatterns.toList()))
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .build()
    }

    class CsrfRequiredMatcher(private val allowedOrigins: List<String>) : RequestMatcher {

        override fun matches(request: HttpServletRequest): Boolean {
            val origin = request.getHeader("Origin")
            val referer = request.getHeader("Referer")

            return allowedOrigins.any { allowedOrigin ->
                matchesWildcard(origin, allowedOrigin) || matchesWildcard(referer, allowedOrigin)
            }
        }

        private fun matchesWildcard(url: String?, wildcardPattern: String): Boolean {
            if (url == null) return false

            val regexPattern = wildcardPattern
                .replace(".", "\\.")
                .replace("*", ".*")
                .toRegex()

            return regexPattern.matches(url)
        }
    }
}
