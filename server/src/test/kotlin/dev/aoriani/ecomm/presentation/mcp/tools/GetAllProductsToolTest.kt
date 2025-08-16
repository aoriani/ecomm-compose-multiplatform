package dev.aoriani.ecomm.presentation.mcp.tools

import dev.aoriani.ecomm.data.repositories.FakeProductRepository
import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.presentation.mcp.models.Products
import io.mockk.mockk
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.TextContent
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetAllProductsToolTest {

    @Test
    fun `When execute is called and use case returns success then it returns products`() = runTest {
        // Given
        val mockProducts = listOf(
            Product(
                id = ProductId("test1"),
                name = "Test Product 1",
                price = BigDecimal("19.99"),
                description = "Test description 1",
                images = listOf("https://example.com/image1.jpg"),
                material = "Test material",
                inStock = true,
                countryOfOrigin = "Test Country"
            ),
            Product(
                id = ProductId("test2"),
                name = "Test Product 2",
                price = BigDecimal("29.99"),
                description = "Test description 2",
                images = listOf("https://example.com/image2.jpg"),
                material = "Test material 2",
                inStock = false,
                countryOfOrigin = "Test Country 2"
            )
        )

        val repository = FakeProductRepository(_getAll = { Result.success(mockProducts) })
        val tool = GetAllProductsTool(GetAllProductsUseCase(repository))

        // Mock the logger field using reflection to avoid log messages during tests
        // Create a mock request
        val request = mockk<CallToolRequest>()

        // When
        val result = tool.execute(request)

        // Then
        // Use direct property access instead of casting
        assertEquals(false, result.isError)
        assertEquals(2, result.content.size)
        assertNotNull(result.structuredContent)

        // Verify the structured content contains the products array
        val structuredContent = result.structuredContent
        assertNotNull(structuredContent)
        assertTrue(structuredContent.toString().contains("products"))
    }

    @Test
    fun `When execute is called and use case returns empty list then it returns empty products list`() = runTest {
        // Given
        val repository = FakeProductRepository(_getAll = { Result.success(emptyList()) })
        val getAllProductsUseCase = GetAllProductsUseCase(repository)

        val tool = GetAllProductsTool(getAllProductsUseCase)

        // Create a mock request
        val request = mockk<CallToolRequest>()

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(false, result.isError)
        assertEquals(0, result.content.size)
        assertNotNull(result.structuredContent)

        // Verify the structured content contains an empty products array
        val structuredContent = result.structuredContent
        assertNotNull(structuredContent)
        assertTrue(structuredContent.toString().contains("products"))
    }

    @Test
    fun `when execute is called and use case returns failure then it returns error`() = runTest {
        // Given
        val exception = RuntimeException("Test error")
        val repository = FakeProductRepository(_getAll = { Result.failure(exception) })
        val getAllProductsUseCase = GetAllProductsUseCase(repository)

        val tool = GetAllProductsTool(getAllProductsUseCase)

        // Create a mock request
        val request = mockk<CallToolRequest>()

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(true, result.isError)
        assertEquals(1, result.content.size)
        val content = result.content[0]
        assertIs<TextContent>(content)
        val text = content.text
        assertEquals("Failed to retrieve products", text)
    }

    @Test
    fun `when tool is created then it has correct properties`() {
        // Given
        val getAllProductsUseCase = mockk<GetAllProductsUseCase>()

        // When
        val tool = GetAllProductsTool(getAllProductsUseCase)

        // Then
        assertEquals("get_products_list", tool.name)
        assertTrue(tool.description.isNotBlank())
        assertNotNull(tool.output)
        assertEquals(Products::class, tool.output)
        assertEquals(null, tool.input)
    }
}