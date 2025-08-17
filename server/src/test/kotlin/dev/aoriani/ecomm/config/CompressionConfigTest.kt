package dev.aoriani.ecomm.config

import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class CompressionConfigTest {

    @Test
    fun `gzip is applied for large text responses`() = testApplication {
        application {
            configureCompression()
            routing {
                get("/big") {
                    val large = buildString { repeat(2000) { append('a') } }
                    call.respondText(large, ContentType.Text.Plain)
                }
            }
        }

        val response = client.get("/big") {
            headers { append(HttpHeaders.AcceptEncoding, "gzip") }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("gzip", response.headers[HttpHeaders.ContentEncoding])
        // Body is compressed; ensure we didn't accidentally echo plain text
        // by checking it's not the original string
        val body = response.bodyAsText()
        // Compressed body will be binary; Ktor decodes to text for convenience, so just sanity check size
        assert(body.length < 2000)
    }
}
