package no.digdir.accessrequestapi.controller

import io.swagger.v3.oas.annotations.tags.Tag
import no.digdir.accessrequestapi.client.FelleskatalogClient
import no.digdir.accessrequestapi.configuration.FdkUrls
import no.digdir.accessrequestapi.model.DataResourceMetadata
import no.digdir.accessrequestapi.model.DatasetLanguage
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
    val fdkUrls: FdkUrls,
) {
    val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(WebClientResponseException.NotFound::class)
    fun handleNotFound(exception: WebClientResponseException) {
        logger.info("Resource not found", exception)
    }

    @PostMapping("/{language}")
    fun resolveDataDef(
        @PathVariable language: DatasetLanguage,
        @RequestBody dataDef: ShoppingCart.DataDef,
    ): ResponseEntity<ResolveDataDefResponse> {
        logger.info("Received request to resolve data definition for resource: $dataDef")

        val resourceId: UUID = dataDef.id
        val resourceType = dataDef.type

        val metadata =
            felleskatalogClient.getMetadata(resourceType.toUrlString(), resourceId) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(
            ResolveDataDefResponse(
                metadata = metadata,
                language = language,
                urlToResource = "${fdkUrls.frontend}/$resourceType/$resourceId",
            ),
        )
    }
}

data class ResolveDataDefResponse(
    val title: String?,
    val description: String?,
    val urlToResource: String?,
    val hintIsPrePublicationData: Boolean,
) {
    constructor(metadata: DataResourceMetadata, language: DatasetLanguage, urlToResource: String?) : this(
        title = metadata.title.get(language),
        description = metadata.description?.get(language),
        urlToResource = urlToResource,
        hintIsPrePublicationData = metadata.isDatasetWithoutDistribution(),
    )
}
