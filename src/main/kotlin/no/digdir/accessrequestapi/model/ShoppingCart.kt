package no.digdir.accessrequestapi.model

data class ShoppingCart(
    val orgnr: String,
    val language: DatasetLanguage,
    val hintIsPublic: Boolean,
    val dataDef: DataDef,
) {
    val system: String = "datanorge"

    data class DataDef(
        val identifier: String?,
        val resourceId: String,
        val orgnr: String,
        val resourceName: String,
    )
}

enum class DatasetLanguage {
    nn,
    nb,
    en,
}
