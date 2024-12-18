package no.digdir.accessrequestapi.client

import no.digdir.accessrequestapi.configuration.FdkUrls
import no.digdir.accessrequestapi.model.DataResourceMetadata
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.util.retry.Retry
import java.time.Duration
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
    ): DataResourceMetadata? {
        logger.info("Fetching metadata for type: $type and id: $id from Felleskatalog.")

        val metadata =
            webClient
                .get()
                .uri("/$type/$id")
                .retrieve()
                .bodyToMono<DataResourceMetadata>()
                .retryWhen(
                    Retry
                        .backoff(3, Duration.ofMillis(500))
                        .doBeforeRetry { logger.info("Retrying due to: ${it.failure().message}", it.failure()) },
                ).block()

        logger.info("Metadata fetched successfully: $metadata")

        return metadata
    }
}
