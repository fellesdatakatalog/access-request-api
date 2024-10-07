package no.digdir.accessrequestapi.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

private val mockserver = WireMockServer(5050)

fun startMockServer() {
    if (!mockserver.isRunning) {
        mockserver.stubFor(
            get(urlEqualTo("/ping"))
                .willReturn(
                    aResponse()
                        .withStatus(200),
                ),
        )

        mockserver.start()
    }
}

fun stopMockServer() {
    if (mockserver.isRunning) mockserver.stop()
}
