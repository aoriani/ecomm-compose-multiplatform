package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
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
}
