package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class McpConfigTest {

    private object DummyRepo : ProductRepository {
        override suspend fun getAll(): Result<List<Product>> = Result.success(emptyList())
        override suspend fun getById(id: ProductId): Result<Product> = Result.failure(NoSuchElementException("n/a"))
    }

    @Test
    fun `configureMcp installs without errors when deps provided`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ecomm.mcp.server-name" to "test-mcp",
                "ecomm.mcp.server-version" to "0.0.0",
            )
        }
        application {
            // Provide required use cases for tools
            val getAll = GetAllProductsUseCase(DummyRepo)
            val getById = GetProductByIdUseCase(DummyRepo)
            dependencies {
                provide<GetAllProductsUseCase> { getAll }
                provide<GetProductByIdUseCase> { getById }
            }

            // Should not throw
            configureMcp()
        }
    }
}

