package no.digdir.accessrequestapi.client

import no.digdir.accessrequestapi.configuration.FdkUrls
import no.digdir.accessrequestapi.model.DatasetMetadata
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.util.UUID

@Component
class FelleskatalogClient(
    fdkUrls: FdkUrls,
) {
    val webClient = WebClient.create(fdkUrls.api)

    fun getMetadata(
        type: String,
        id: UUID,
    ): DatasetMetadata? =
        webClient
            .get()
            .uri("/$type/$id")
            .retrieve()
            .bodyToMono<DatasetMetadata>()
            .block()
}
