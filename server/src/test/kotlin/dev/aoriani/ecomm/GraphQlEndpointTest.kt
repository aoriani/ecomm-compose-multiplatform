package dev.aoriani.ecomm

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.ApplicationTestBuilder
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
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphQlEndpointTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun buildGraphQLRequest(query: String, variables: Map<String, Any>? = null): String {
        return buildJsonObject {
            put("query", JsonPrimitive(query))
            variables?.let {
                val variablesJson = buildJsonObject {
                    for ((key, value) in variables) {
                        when (value) {
                            is String -> put(key, JsonPrimitive(value))
                            is Number -> put(key, JsonPrimitive(value))
                            is Boolean -> put(key, JsonPrimitive(value))
                            else -> put(key, JsonPrimitive(value.toString()))
                        }
                    }
                }
                put("variables", variablesJson)
            }
        }.toString()
    }

    private fun ApplicationTestBuilder.configureAppWithMockedProducts(mockProductRepository: ProductRepository) {
        application {
            dependencies.provide<ProductRepository> {
                mockProductRepository
            }
            module()
        }
    }

    private fun ApplicationTestBuilder.configureEnvironment() {
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:sqlite:./data/products.db",
                "ecomm.database.driver" to "org.sqlite.JDBC",
                "ecomm.images.base-url" to "http://localhost:8080/static/images"
            )
        }
    }

    @Test
    fun `When products query returns an empty list then it should return an empty list`() = testApplication {
        val mockProductRepository: ProductRepository = mockk {
            coEvery { getAll() } returns emptyList()
        }
        configureEnvironment()
        configureAppWithMockedProducts(mockProductRepository)

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
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())

        val jsonResponse = json.parseToJsonElement(response.bodyAsText())
        assertTrue(jsonResponse.jsonObject["data"]?.jsonObject?.get("products")?.jsonArray?.isEmpty() == true)
    }

    @Test
    fun `When products query returns products then it should return the products`() = testApplication {
        val mockProductRepository: ProductRepository = mockk {
            coEvery { getAll() } returns listOf(
                Product(
                    id = ID("1"),
                    name = "Product 1",
                    description = "Description 1",
                    price = BigDecimal.TEN,
                    images = listOf("http://localhost:8080/static/images/image1.jpg"),
                    material = "Cotton",
                    inStock = true,
                    countryOfOrigin = "USA"
                )
            )
        }
        configureEnvironment()
        configureAppWithMockedProducts(mockProductRepository)

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
        assertEquals(HttpStatusCode.OK, response.status)

        val jsonResponse = json.parseToJsonElement(response.bodyAsText())
        val products = jsonResponse.jsonObject["data"]?.jsonObject?.get("products")?.jsonArray
        assertNotNull(products)
        assertEquals(1, products.size)
        assertEquals("1", products[0].jsonObject["id"]?.jsonPrimitive?.content)
        assertEquals("Product 1", products[0].jsonObject["name"]?.jsonPrimitive?.content)
    }

    @Test
    fun `When product query with existing product then it should return the product`() = testApplication {
        val mockProductRepository: ProductRepository = mockk {
            coEvery { getById("1") } returns Product(
                id = ID("1"),
                name = "Product 1",
                description = "Description 1",
                price = BigDecimal.TEN,
                images = listOf("http://localhost:8080/static/images/image1.jpg"),
                material = "Cotton",
                inStock = true,
                countryOfOrigin = "USA"
            )
        }
        configureEnvironment()
        configureAppWithMockedProducts(mockProductRepository)

        val query = $$"""
            query GetProduct($id: ID!) {
                product(id: $id) {
                    id
                    name
                }
            }
        """.trimIndent()
        val variables = mapOf("id" to "1")
        val response = client.post("/graphql") {
            contentType(ContentType.Application.Json)
            setBody(buildGraphQLRequest(query, variables))
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val jsonResponse = json.parseToJsonElement(response.bodyAsText())
        val product = jsonResponse.jsonObject["data"]?.jsonObject?.get("product")?.jsonObject
        assertNotNull(product)
        assertEquals("1", product["id"]?.jsonPrimitive?.content)
        assertEquals("Product 1", product["name"]?.jsonPrimitive?.content)
    }

    @Test
    fun `When product query with non-existing product then it should return null`() = testApplication {
        val mockProductRepository: ProductRepository = mockk {
            coEvery { getById("2") } returns null
        }
        configureEnvironment()
        configureAppWithMockedProducts(mockProductRepository)

        val query = $$"""
            query GetProduct($id: ID!) {
                product(id: $id) {
                    id
                    name
                }
            }
        """.trimIndent()
        val variables = mapOf("id" to "2")
        val response = client.post("/graphql") {
            contentType(ContentType.Application.Json)
            setBody(buildGraphQLRequest(query, variables))
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val jsonResponse = json.parseToJsonElement(response.bodyAsText())
        assertNull(jsonResponse.jsonObject["data"]?.jsonObject?.get("product"))
    }

    @Test
    fun `When a malformed query is sent then it should return an error`() = testApplication {
        val mockProductRepository: ProductRepository = mockk()
        configureEnvironment()
        configureAppWithMockedProducts(mockProductRepository)

        val query = "query { products { id, name } }"
        val response = client.post("/graphql") {
            contentType(ContentType.Application.Json)
            setBody(buildGraphQLRequest(query))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val jsonResponse = json.parseToJsonElement(response.bodyAsText())
        assertNotNull(jsonResponse.jsonObject["errors"])
    }
}