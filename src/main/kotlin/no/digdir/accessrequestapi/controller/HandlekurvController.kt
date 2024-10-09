package no.digdir.accessrequestapi.controller

import no.digdir.accessrequestapi.client.FelleskatalogClient
import no.digdir.accessrequestapi.model.Handlekurv
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.UUID

@RestController
@RequestMapping(value = ["/handlekurv"], produces = ["application/json"])
class HandlekurvController(
    @Value("\${url.fellesdatakatalog.frontend}")
    val fellesdatakatalogFrontendUrl: String,
    val felleskatalogClient: FelleskatalogClient,
) {
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(WebClientResponseException.NotFound::class)
    fun handleNotFound() {}

    @GetMapping("/{type}/{id}")
    fun getHandlekurv(
        @PathVariable type: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Handlekurv> {
        val metadata =
            felleskatalogClient.getMetadata(type, id)
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(metadata.toHandlekurv(resourceId = "$fellesdatakatalogFrontendUrl/$type/$id"))
    }
}
