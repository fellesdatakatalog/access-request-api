package no.digdir.accessrequestapi.client

import no.digdir.accessrequestapi.configuration.KudafUrls
import no.digdir.accessrequestapi.model.ShoppingCart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.util.retry.Retry
import java.time.Duration

@Component
class KudafClient(
    kudafUrls: KudafUrls,
) {
    val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    val webClient = WebClient.create(kudafUrls.soknadApi)

    fun warmUp() {
        logger.info("Starting warm-up by checking the health endpoint.")
        webClient
            .get()
            .uri("/health")
            .retrieve()
            .toBodilessEntity()
            .retryWhen(
                Retry.backoff(3, Duration.ofSeconds(2)),
            ).block()
    }

    fun getRedirectUrl(cart: ShoppingCart): String? {
        logger.info("Fetching redirect URL for cart: $cart")
        warmUp()
        return webClient
            .post()
            .uri("/cart")
            .bodyValue(cart)
            .retrieve()
            .bodyToMono<KudafAccessRequestResponse>()
            .block()
            ?.redirectUrl
    }
}

data class KudafAccessRequestResponse(
    val redirectUrl: String,
)
