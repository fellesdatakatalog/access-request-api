package no.digdir.accessrequestapi.client

import no.digdir.accessrequestapi.model.DatasetMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.util.UUID

@Component
class FelleskatalogClient(
    @Value("\${url.fellesdatakatalog.api}")
    private val fellesdatakatalogApiUrl: String,
) {
    val webClient =
        WebClient.create(fellesdatakatalogApiUrl)

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
