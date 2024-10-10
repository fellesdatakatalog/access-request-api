package no.digdir.accessrequestapi.model

data class DatasetMetadata(
    val accessRequestUrl: String?,
    val contactPoint: List<ContactPoint>,
    val title: LocalizedStrings,
    val publisher: Publisher,
    val identifier: Array<String>?,
    val accessRights: AccessRights?,
    val description: LocalizedStrings?,
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
        val code: AccessRight,
    )

    fun toShoppingCart(
        resourceId: String,
        language: DatasetLanguage,
    ): ShoppingCart =
        ShoppingCart(
            orgnr = publisher.id,
            hintIsPublic = accessRights?.code == AccessRight.PUBLIC,
            dataDef =
                ShoppingCart.DataDef(
                    identifier = identifier?.firstOrNull(),
                    resourceId = resourceId,
                    orgnr = publisher.id,
                    resourceName = title.get(language) ?: "",
                ),
            language = language,
        )
}

enum class AccessRight {
    PUBLIC,
    RESTRICTED,
    NON_PUBLIC,
}
