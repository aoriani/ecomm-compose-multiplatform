package dev.aoriani.ecomm.config

import io.ktor.client.request.options
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.options
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CorsConfigTest {

    @Test
    fun `preflight request from allowed origin returns CORS headers`() = testApplication {
        application {
            configureCors()
            routing {
                // Explicit options route to exercise CORS preflight
                options("/resource") { call.respond(HttpStatusCode.OK) }
            }
        }

        val response = client.options("/resource") {
            headers {
                append(HttpHeaders.Origin, "http://localhost:8080")
                append(HttpHeaders.AccessControlRequestMethod, HttpMethod.Get.value)
                append(HttpHeaders.AccessControlRequestHeaders, HttpHeaders.ContentType)
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://localhost:8080", response.headers[HttpHeaders.AccessControlAllowOrigin])
        assertTrue(response.headers[HttpHeaders.AccessControlAllowMethods]?.contains("GET") == true)
    }
}
