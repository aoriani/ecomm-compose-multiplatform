package dev.aoriani.ecomm.config

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusPagesConfigTest {

    @Test
    fun `unhandled exception is mapped to 500`() = testApplication {
        application {
            configureStatusPages()
            routing {
                get("/boom") {
                    throw RuntimeException("boom")
                }
                // control route
                get("/ok") { call.respond(HttpStatusCode.OK) }
            }
        }

        val ok = client.get("/ok")
        assertEquals(HttpStatusCode.OK, ok.status)

        val res = client.get("/boom")
        assertEquals(HttpStatusCode.InternalServerError, res.status)
    }
}

