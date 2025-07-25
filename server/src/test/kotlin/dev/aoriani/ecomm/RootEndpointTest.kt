package dev.aoriani.ecomm

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class RootEndpointTest {

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:./data/products.db",
                "ecomm.database.driver" to "org.sqlite.JDBC",
                "ecomm.images.base-url" to "http://localhost:8080/static/images" // Added for test configuration
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