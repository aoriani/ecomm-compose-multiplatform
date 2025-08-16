package dev.aoriani.ecomm

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RootEndpointTest {

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:./data/products.db",
                "ecomm.database.driver" to "org.sqlite.JDBC",
                "ecomm.images.base-url" to "http://localhost:8080/static/images", // Added for test configuration
                "ecomm.mcp.server-name" to "mcp-server",
                "ecomm.mcp.server-version" to "1.0.0",
            )
        }
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.Companion.OK, status)
            assertEquals(ContentType.Text.Plain.withCharset(Charsets.UTF_8), this.contentType())
            assertEquals("Hello World!", this.bodyAsText())
        }
    }

}