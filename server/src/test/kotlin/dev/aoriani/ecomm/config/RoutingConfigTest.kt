package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.module
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

class RoutingConfigTest {

    @Test
    fun `graphiQL route is available`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:./data/products.db",
                "ecomm.database.driver" to "org.sqlite.JDBC",
                "ecomm.images.base-url" to "http://localhost:8080/static/images",
                "ecomm.mcp.server-name" to "mcp-server",
                "ecomm.mcp.server-version" to "1.0.0",
            )
        }
        application { module() }

        val response = client.get("/graphiql")
        assertEquals(HttpStatusCode.OK, response.status)
        // GraphiQL returns HTML
        assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())
        // Ensure content contains GraphiQL marker
        assert(response.bodyAsText().contains("GraphiQL"))
    }
}

