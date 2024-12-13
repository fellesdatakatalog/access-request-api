package no.digdir.accessrequestapi.model

@Deprecated("Use the result of https://github.com/Informasjonsforvaltning/applicationdcat-ap-no/issues/2 when it is ready")
val HARD_CODED_RESOURCE_ID_IS_ORG_ONLY = listOf(
    "https://staging.fellesdatakatalog.digdir.no/data-services/ecdbd6d4-7026-3731-bd8e-2f15e26221a2",
    "https://data.norge.no/data-services/031f7cea-4920-3cd4-8333-7f8992904aa5"
)

data class DatasetMetadata(
    val accessRequestUrl: String?,
    val contactPoint: List<ContactPoint>?,
    val title: LocalizedStrings,
    val publisher: Publisher,
    val identifier: Array<String>?,
    val accessRights: AccessRights?,
    val description: LocalizedStrings?,
    val distribution: List<Any>?,
    val type: String,
) {
    data class ContactPoint(
        val email: String,
    )

    data class LocalizedStrings(
        val nb: String?,
        val nn: String?,
        val no: String?,
        val en: String?,
    ) {
        fun get(language: DatasetLanguage): String? =
            when (language) {
                DatasetLanguage.nn -> nn
                DatasetLanguage.nb -> nb
                DatasetLanguage.en -> en
            } ?: getBestEffort()

        private fun getBestEffort(): String? = en ?: nb ?: nn ?: no
    }

    data class Publisher(
        val id: String,
    )

    data class AccessRights(
        val code: AccessRight?,
    )

    fun toShoppingCart(
        resourceId: String,
        language: DatasetLanguage,
    ): ShoppingCart =
        ShoppingCart(
            orgnr = publisher.id,
            hintIsPublic = accessRights?.code == AccessRight.PUBLIC,
            hintIsOrg = resourceId in HARD_CODED_RESOURCE_ID_IS_ORG_ONLY,
            hintIsPrePublicationData = isDatasetWithoutDistribution(),
            dataDef =
                ShoppingCart.DataDef(
                    identifier = identifier?.firstOrNull(),
                    resourceId = resourceId,
                    orgnr = publisher.id,
                    resourceName = title.get(language) ?: "",
                ),
            language = language,
        )

    fun isDatasetWithoutDistribution() = type == "datasets" && distribution.isNullOrEmpty()
}

enum class AccessRight {
    PUBLIC,
    RESTRICTED,
    NON_PUBLIC,
}
