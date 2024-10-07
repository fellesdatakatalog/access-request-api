package no.digdir.accessrequestapi.utils

import org.springframework.http.HttpStatus
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URI

fun apiGet(
    port: Int,
    endpoint: String,
    acceptHeader: String?,
): Map<String, Any> =
    try {
        val connection = URI("http://localhost:$port$endpoint").toURL().openConnection()
        if (connection !is HttpURLConnection) {
            throw Exception("Connection is not an instance of HttpURLConnection")
        }

        if (acceptHeader != null) connection.setRequestProperty("Accept", acceptHeader)
        connection.connect()

        if (isOK(connection.responseCode)) {
            val responseBody = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            mapOf(
                "body" to responseBody,
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode,
            )
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body" to " ",
            )
        }
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body" to " ",
        )
    }

private fun isOK(response: Int?): Boolean =
    if (response == null) {
        false
    } else {
        HttpStatus.resolve(response)?.is2xxSuccessful == true
    }
