package no.digdir.accessrequestapi.controller

import io.swagger.v3.oas.annotations.tags.Tag
import no.digdir.accessrequestapi.client.FelleskatalogClient
import no.digdir.accessrequestapi.client.KudafClient
import no.digdir.accessrequestapi.configuration.FdkUrls
import no.digdir.accessrequestapi.model.DatasetLanguage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.UUID

@Tag(name = "Access request")
@RestController
@RequestMapping(value = ["/access-request"], produces = ["application/json"])
class AccessRequestController(
    val fdkUrls: FdkUrls,
    val felleskatalogClient: FelleskatalogClient,
    val kudafClient: KudafClient,
) {
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(WebClientResponseException.NotFound::class)
    fun handleNotFound() {}

    @PostMapping("/{language}/{type}/{id}")
    fun createKudafApplication(
        @PathVariable language: DatasetLanguage,
        @PathVariable type: String,
        @PathVariable id: UUID,
    ): ResponseEntity<String> {
        val metadata = felleskatalogClient.getMetadata(type, id) ?: return ResponseEntity.notFound().build()

        val shoppingCart =
            metadata.toShoppingCart(resourceId = "${fdkUrls.frontend}/$type/$id", language = language)

        val redirectUrl = kudafClient.getRedirectUrl(shoppingCart) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(redirectUrl)
    }
}
