package no.digdir.accessrequestapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("url.kudaf")
data class KudafUrls(
    val soknadApi: String,
)
