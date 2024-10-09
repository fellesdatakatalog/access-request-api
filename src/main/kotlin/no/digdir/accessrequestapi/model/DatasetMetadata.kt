package no.digdir.accessrequestapi.model

data class DatasetMetadata(
    val contactPoint: List<ContactPoint>,
    val title: Title,
    val publisher: Publisher,
    val identifier: Array<String>?,
    val accessRights: AccessRights?,
    val description: Description?,
) {
    data class ContactPoint(
        val email: String,
    )

    data class Title(
        val nb: String?,
        val en: String?,
    )

    data class Description(
        val nb: String?,
        val en: String?,
    )

    data class Publisher(
        val id: String,
    )

    data class AccessRights(
        val code: AccessRight,
    )

    fun toHandlekurv(resourceId: String): Handlekurv =
        Handlekurv(
            orgnr = publisher.id,
            hintIsPublic = accessRights?.code == AccessRight.PUBLIC,
            dataDef =
                Handlekurv.DataDef(
                    identifier = identifier?.firstOrNull(),
                    resourceId = resourceId,
                    orgnr = publisher.id,
                    resourceName = title.nb ?: title.en ?: "",
                ),
            language = DatasetLanguage.nb,
        )
}

enum class AccessRight {
    PUBLIC,
    RESTRICTED,
    NON_PUBLIC,
}
