package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class McpConfigTest {

    private object FakeProductRepository : ProductRepository {
        override suspend fun getAll(): Result<List<Product>> = Result.success(emptyList())
        override suspend fun getById(id: ProductId): Result<Product> = Result.failure(NoSuchElementException("n/a"))
    }

    private fun mcpConfig() = MapApplicationConfig(
        "ecomm.mcp.server-name" to "test-mcp",
        "ecomm.mcp.server-version" to "0.0.0",
    ).apply {
        put("ecomm.mcp.allowed-hosts", listOf("localhost", "127.0.0.1", "[::1]"))
        put("ecomm.mcp.allowed-origins", listOf("http://localhost", "http://127.0.0.1", "http://[::1]"))
    }

    private fun Application.installMcpForTest() {
        val getAll = GetAllProductsUseCase(FakeProductRepository)
        val getById = GetProductByIdUseCase(FakeProductRepository)
        dependencies {
            provide<GetAllProductsUseCase> { getAll }
            provide<GetProductByIdUseCase> { getById }
        }
        configureMcp()
    }

    @Test
    fun `configureMcp installs without errors when deps provided`() = testApplication {
        environment {
            config = mcpConfig()
        }
        application {
            installMcpForTest()
        }
    }

    @Test
    fun `MCP accepts a configured host`() = testApplication {
        environment { config = mcpConfig() }
        application { installMcpForTest() }

        val response = client.get("/mcp") {
            header(HttpHeaders.Host, "localhost")
        }

        assertNotEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `MCP rejects an unconfigured host`() = testApplication {
        environment { config = mcpConfig() }
        application { installMcpForTest() }

        val response = client.get("/mcp") {
            header(HttpHeaders.Host, "malicious.example")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertTrue(response.bodyAsText().contains("Invalid Host"))
    }

    @Test
    fun `MCP rejects an unconfigured origin`() = testApplication {
        environment { config = mcpConfig() }
        application { installMcpForTest() }

        val response = client.get("/mcp") {
            header(HttpHeaders.Host, "localhost")
            header(HttpHeaders.Origin, "https://malicious.example")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertTrue(response.bodyAsText().contains("Invalid Origin"))
    }

    @Test
    fun `MCP completes the initialization handshake and lists tools`() = testApplication {
        environment { config = mcpConfig() }
        application { installMcpForTest() }

        val initializeResponse = client.post("/mcp") {
            header(HttpHeaders.Host, "localhost")
            header(HttpHeaders.Origin, "http://localhost")
            header(HttpHeaders.Accept, "application/json, text/event-stream")
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "jsonrpc": "2.0",
                  "id": 1,
                  "method": "initialize",
                  "params": {
                    "protocolVersion": "2025-11-25",
                    "capabilities": {},
                    "clientInfo": {
                      "name": "integration-test-client",
                      "version": "1.0.0"
                    }
                  }
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.OK, initializeResponse.status)
        val sessionId = assertNotNull(initializeResponse.headers["mcp-session-id"])
        val responseJson = Json.parseToJsonElement(initializeResponse.bodyAsText()).jsonObject
        assertEquals("2.0", responseJson.getValue("jsonrpc").jsonPrimitive.content)
        assertEquals("1", responseJson.getValue("id").jsonPrimitive.content)

        val result = responseJson.getValue("result").jsonObject
        assertEquals("2025-11-25", result.getValue("protocolVersion").jsonPrimitive.content)
        val serverInfo = result.getValue("serverInfo").jsonObject
        assertEquals("test-mcp", serverInfo.getValue("name").jsonPrimitive.content)
        assertEquals("0.0.0", serverInfo.getValue("version").jsonPrimitive.content)
        assertTrue(result.getValue("capabilities").jsonObject.containsKey("tools"))

        val initializedResponse = client.post("/mcp") {
            header(HttpHeaders.Host, "localhost")
            header(HttpHeaders.Origin, "http://localhost")
            header(HttpHeaders.Accept, "application/json, text/event-stream")
            header("mcp-session-id", sessionId)
            header("mcp-protocol-version", "2025-11-25")
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "jsonrpc": "2.0",
                  "method": "notifications/initialized"
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.Accepted, initializedResponse.status)

        val listToolsResponse = client.post("/mcp") {
            header(HttpHeaders.Host, "localhost")
            header(HttpHeaders.Origin, "http://localhost")
            header(HttpHeaders.Accept, "application/json, text/event-stream")
            header("mcp-session-id", sessionId)
            header("mcp-protocol-version", "2025-11-25")
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "jsonrpc": "2.0",
                  "id": 2,
                  "method": "tools/list",
                  "params": {}
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.OK, listToolsResponse.status)
        val listToolsJson = Json.parseToJsonElement(listToolsResponse.bodyAsText()).jsonObject
        assertEquals("2", listToolsJson.getValue("id").jsonPrimitive.content)
        val toolNames = listToolsJson
            .getValue("result")
            .jsonObject
            .getValue("tools")
            .jsonArray
            .map { it.jsonObject.getValue("name").jsonPrimitive.content }
            .toSet()
        assertEquals(setOf("get_products_list", "get_product_by_id"), toolNames)
    }
}
