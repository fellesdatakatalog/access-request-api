package no.digdir.accessrequestapi.client

import no.digdir.accessrequestapi.configuration.KudafUrls
import no.digdir.accessrequestapi.model.ShoppingCart
import org.slf4j.Logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class KudafClient(kudafUrls: KudafUrls) {
    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    private val webClient: WebClient = WebClient.create(kudafUrls.soknadApi)

    fun getRedirectUrl(cart: ShoppingCart): String? {
        logger.info("Fetching redirect URL for cart: $cart")

        val redirectUrl =
            webClient
                .post()
                .uri("/cart")
                .bodyValue(cart)
                .retrieve()
                .bodyToMono<KudafAccessRequestResponse>()
                .block()
                ?.redirectUrl

        logger.info("Redirect URL: $redirectUrl")
        return redirectUrl
    }
}

data class KudafAccessRequestResponse(
    val redirectUrl: String,
)
