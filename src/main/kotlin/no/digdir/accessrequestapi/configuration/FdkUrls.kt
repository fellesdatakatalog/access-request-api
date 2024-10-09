package no.digdir.accessrequestapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("url.fellesdatakatalog")
data class FdkUrls(
    val api: String,
    val frontend: String,
)
