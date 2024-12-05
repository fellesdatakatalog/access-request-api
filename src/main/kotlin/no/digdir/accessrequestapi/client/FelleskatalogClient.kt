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
    val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    val webClient = WebClient.create(fdkUrls.api)

    fun getMetadata(
        type: String,
        id: UUID,
    ): DatasetMetadata? {
        logger.info("Fetching metadata for type: $type and id: $id from Felleskatalog.")

        return webClient
            .get()
            .uri("/$type/$id")
            .retrieve()
            .bodyToMono<DatasetMetadata>()
            .block()
    }
}
