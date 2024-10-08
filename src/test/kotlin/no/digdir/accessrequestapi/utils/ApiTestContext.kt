package no.digdir.accessrequestapi.utils

import org.springframework.boot.test.web.server.LocalServerPort

abstract class ApiTestContext {
    @LocalServerPort
    var port = 0
}
