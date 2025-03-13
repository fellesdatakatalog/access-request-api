package no.digdir.accessrequestapi.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.digdir.accessrequestapi.client.KudafAccessRequestResponse
import no.digdir.accessrequestapi.model.DataResourceMetadata
import no.digdir.accessrequestapi.model.DataResourceType
import no.digdir.accessrequestapi.model.DatasetLanguage
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import java.util.*

@Tag("integration")
@ActiveProfiles("test")

@AutoConfigureMockMvc
@EnableWireMock(ConfigureWireMock(port = 6000))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccessRequestTest {

    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `access request returns OK`() {
        val language = DatasetLanguage.en
        val type = DataResourceType.datasets
        val id = UUID.randomUUID()

        stubFor(
            get("/$type/$id").willReturn(
                okJson(jacksonObjectMapper.writeValueAsString(dataResourceMetadata(id, type)))
            )
        )

        stubFor(
            post("/cart").willReturn(
                okJson(jacksonObjectMapper.writeValueAsString(accessRequestResponse()))
            )
        )

        mockMvc.post("/access-request/$language/$type/$id").andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                string("redirect")
            }
        }
    }

    @Test
    fun `access request returns not found on missing dataset`() {
        val language = DatasetLanguage.en
        val type = DataResourceType.datasets
        val id = UUID.randomUUID()

        stubFor(
            get("/$type/$id").willReturn(
                notFound()
            )
        )

        mockMvc.post("/access-request/$language/$type/$id").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `access request returns not found on missing application`() {
        val language = DatasetLanguage.en
        val type = DataResourceType.datasets
        val id = UUID.randomUUID()

        stubFor(
            get("/$type/$id").willReturn(
                okJson(jacksonObjectMapper.writeValueAsString(dataResourceMetadata(id, type)))
            )
        )

        stubFor(
            post("/cart").willReturn(
                notFound()
            )
        )

        mockMvc.post("/access-request/$language/$type/$id").andExpect {
            status { isNotFound() }
        }
    }

    private fun accessRequestResponse() = KudafAccessRequestResponse(redirectUrl = "redirect")

    private fun dataResourceMetadata(id: UUID, type: DataResourceType) = DataResourceMetadata(
        title = DataResourceMetadata.LocalizedStrings(
            en = "title",
            nb = null,
            nn = null,
            no = null
        ),
        publisher = DataResourceMetadata.Publisher(
            id = "12345"
        ),
        id = id,
        uri = "http://localhost:6000/datasets/$id",
        type = type,
        accessRequestUrl = null,
        contactPoint = null,
        accessRights = null,
        description = null,
        distribution = null,
    )
}
