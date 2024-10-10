package no.digdir.accessrequestapi.controller

import io.swagger.v3.oas.annotations.tags.Tag
import no.digdir.accessrequestapi.client.FelleskatalogClient
import no.digdir.accessrequestapi.model.DatasetLanguage
import no.digdir.accessrequestapi.model.DatasetMetadata
import no.digdir.accessrequestapi.model.ShoppingCart
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.UUID

@Tag(name = "DataDef resolver")
@RestController
@RequestMapping(value = ["/datadef-resolver"], produces = ["application/json"])
class KudafResolverController(
    val felleskatalogClient: FelleskatalogClient,
) {
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(WebClientResponseException.NotFound::class)
    fun handleNotFound() {}

    @PostMapping("/{language}")
    fun resolveDataDef(
        @PathVariable language: DatasetLanguage,
        @RequestBody dataDef: ShoppingCart.DataDef,
    ): ResponseEntity<ResolveDataDefResponse> {
        val uriReversed =
            java.net
                .URI(dataDef.resourceId)
                .path
                .split("/")
                .filter { it.isNotEmpty() }
                .asReversed()

        val resourceId: UUID = UUID.fromString(uriReversed[0])
        val resourceType = uriReversed[1]

        val metadata =
            felleskatalogClient.getMetadata(resourceType, resourceId) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(ResolveDataDefResponse(metadata, language))
    }
}

data class ResolveDataDefResponse(
    val title: String?,
    val description: String?,
) {
    constructor(metadata: DatasetMetadata, language: DatasetLanguage) : this(
        title = metadata.title.get(language),
        description = metadata.description?.get(language),
    )
}
