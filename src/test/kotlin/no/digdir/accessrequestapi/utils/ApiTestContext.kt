package no.digdir.accessrequestapi.utils

import org.springframework.boot.test.web.server.LocalServerPort
import java.net.HttpURLConnection
import java.net.URI

abstract class ApiTestContext {
    @LocalServerPort
    var port = 0

    companion object {
        init {
            startMockServer()

            try {
                val con = URI("http://localhost:5050/ping").toURL().openConnection().also { it.connect() }
                if (con !is HttpURLConnection || con.responseCode != 200) {
                    stopMockServer()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopMockServer()
            }
        }
    }
}
