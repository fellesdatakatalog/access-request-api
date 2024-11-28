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
    val webClient = WebClient.create(kudafUrls.soknadApi)

    fun getRedirectUrl(cart: ShoppingCart): String? =
        webClient
            .post()
            .uri("/cart")
            .bodyValue(cart)
            .retrieve()
            .bodyToMono<KudafAccessRequestResponse>()
            .retryWhen(
                Retry.backoff(3, Duration.ofSeconds(2)),
            ).block()
            ?.redirectUrl
}

data class KudafAccessRequestResponse(
    val redirectUrl: String,
)
