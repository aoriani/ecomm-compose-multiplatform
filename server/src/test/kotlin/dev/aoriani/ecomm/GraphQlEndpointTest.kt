package dev.aoriani.ecomm

import dev.aoriani.ecomm.repository.ProductRepository
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.charsets.Charsets
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQlEndpointTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun buildGraphQLRequest(query: String, variables: Map<String, Any>? = null): String {
        return buildJsonObject {
            put("query", JsonPrimitive(query))
            variables?.let {
                put("variables", Json.encodeToJsonElement<Map<String, Any>>(it))
            }
        }.toString()
    }

    @Test
    fun testGraphQl() = testApplication {

        val mockProductRepository: ProductRepository = mockk() {
            coEvery { getAll() } returns emptyList()
        }

        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:./data/products.db",
                "ecomm.database.driver" to "org.sqlite.JDBC",
                "ecomm.images.base-url" to "http://localhost:8080/static/images" // Added for test configuration
            )
        }
        application {
            dependencies.provide<ProductRepository> {
                mockProductRepository
            }
            module()
        }

        val query = """
            query {
                products {
                    id
                    name
                }
            }
        """.trimIndent()
        val response = client.post("/graphql") {
            contentType(ContentType.Application.Json)
            setBody(buildGraphQLRequest(query))
        }
        assertEquals(HttpStatusCode.Companion.OK, response.status)
        assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())

        val jsonResponse = json.parseToJsonElement(response.bodyAsText())
        assertTrue(jsonResponse.jsonObject["data"]?.jsonObject["products"]?.jsonArray?.isEmpty() == true)
    }

}