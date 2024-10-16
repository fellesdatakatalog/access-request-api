package no.digdir.accessrequestapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.cors")
class CorsProperties(
    val originPatterns: Array<String>,
)
