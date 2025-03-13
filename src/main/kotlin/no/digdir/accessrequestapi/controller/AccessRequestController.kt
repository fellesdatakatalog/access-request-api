package no.digdir.accessrequestapi.controller

import io.swagger.v3.oas.annotations.tags.Tag
import no.digdir.accessrequestapi.client.FdkClient
import no.digdir.accessrequestapi.client.KudafClient
import no.digdir.accessrequestapi.configuration.FdkUrls
import no.digdir.accessrequestapi.model.DatasetLanguage
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Tag(name = "Access request")
@RestController
@RequestMapping(value = ["/access-request"], produces = ["application/json"])
class AccessRequestController(
    private val fdkUrls: FdkUrls,
    private val fdkClient: FdkClient,
    private val kudafClient: KudafClient
) {
    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(WebClientResponseException.NotFound::class)
    fun handleNotFound(exception: WebClientResponseException) {
        logger.info("Resource not found", exception)
    }

    @PostMapping("/{language}/{type}/{id}")
    fun createKudafApplication(
        @PathVariable language: DatasetLanguage,
        @PathVariable type: String,
        @PathVariable id: UUID
    ): ResponseEntity<String> {
        logger.info("Received request to create Kudaf application for type: $type, id: $id, language: $language")

        val metadata = fdkClient.getMetadata(type, id) ?: return ResponseEntity.notFound().build()

        val shoppingCart =
            metadata.toShoppingCart(urlToResource = "${fdkUrls.frontend}/$type/$id", language = language)

        val redirectUrl = kudafClient.getRedirectUrl(shoppingCart) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(redirectUrl)
    }
}
