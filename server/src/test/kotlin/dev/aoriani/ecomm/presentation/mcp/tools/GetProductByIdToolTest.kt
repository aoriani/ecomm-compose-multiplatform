package dev.aoriani.ecomm.presentation.mcp.tools

import dev.aoriani.ecomm.data.repositories.FakeProductRepository
import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.BlankProductIdException
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.mcp.models.GetProductByIdRequest
import dev.aoriani.ecomm.presentation.mcp.models.Product as McpProduct
import io.mockk.coEvery
import io.mockk.mockk
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.slf4j.Logger
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetProductByIdToolTest {

    @Test
    fun `When execute is called with valid ID and use case returns success then it returns product`() = runTest {
        // Given
        val productId = ProductId("test1")
        val mockProduct = Product(
            id = productId,
            name = "Test Product 1",
            price = BigDecimal("19.99"),
            description = "Test description 1",
            images = listOf("https://example.com/image1.jpg"),
            material = "Test material",
            inStock = true,
            countryOfOrigin = "Test Country"
        )

        val repository = FakeProductRepository(_getById = { Result.success(mockProduct) })
        val getProductByIdUseCase = GetProductByIdUseCase(repository)

        val tool = GetProductByIdTool(getProductByIdUseCase)

        // Create request with arguments
        val request = mockk<CallToolRequest>()
        val requestArgs = GetProductByIdRequest("test1")
        val jsonArgs = Json.encodeToJsonElement(requestArgs).jsonObject
        coEvery { request.arguments } returns jsonArgs

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(false, result.isError)
        assertEquals(1, result.content.size)
        assertNotNull(result.structuredContent)

        // Verify the structured content contains the product fields
        val structuredContent = result.structuredContent
        assertNotNull(structuredContent)
        val structuredString = structuredContent.toString()
        assertTrue(structuredString.contains("id"))
        assertTrue(structuredString.contains("name"))
        assertTrue(structuredString.contains("price"))
    }

    @Test
    fun `When execute is called with blank ID then it returns error`() = runTest {
        // Given
        val getProductByIdUseCase = mockk<GetProductByIdUseCase>()
        val tool = GetProductByIdTool(getProductByIdUseCase)

        // Create request with blank ID
        val request = mockk<CallToolRequest>()
        val requestArgs = GetProductByIdRequest("")
        val jsonArgs = Json.encodeToJsonElement(requestArgs).jsonObject
        coEvery { request.arguments } returns jsonArgs

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(true, result.isError)
        assertEquals(1, result.content.size)
        val content = result.content[0]
        assertIs<TextContent>(content)
        val text = content.text
        assertEquals("Product ID must not be blank", text)
    }

    @Test
    fun `When execute is called with invalid arguments then it returns error`() = runTest {
        // Given
        val getProductByIdUseCase = mockk<GetProductByIdUseCase>()
        val tool = GetProductByIdTool(getProductByIdUseCase)


        // Create request with invalid arguments
        val request = mockk<CallToolRequest>()
        coEvery { request.arguments } returns JsonObject(emptyMap())

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(true, result.isError)
        assertEquals(1, result.content.size)
        val content = result.content[0]
        assertIs<TextContent>(content)
        val text = content.text
        assertEquals("Product ID must not be blank", text)
    }

    @Test
    fun `when execute is called and use case returns ProductNotFoundException then it returns error`() = runTest {
        // Given
        val productId = ProductId("nonexistent")
        val exception = ProductNotFoundException(productId)
        val repository = FakeProductRepository(_getById = { Result.failure(exception) })

        val getProductByIdUseCase = GetProductByIdUseCase(repository)

        val tool = GetProductByIdTool(getProductByIdUseCase)

        // Create request with arguments
        val request = mockk<CallToolRequest>()
        val requestArgs = GetProductByIdRequest("nonexistent")
        val jsonArgs = Json.encodeToJsonElement(requestArgs).jsonObject
        coEvery { request.arguments } returns jsonArgs

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(true, result.isError)
        assertEquals(1, result.content.size)
        val content = result.content[0]
        assertIs<TextContent>(content)
        val text = content.text
        assertEquals("Product not found: nonexistent", text)
    }

    @Test
    fun `When execute is called and use case returns BlankProductIdException then it returns error`() = runTest {
        // Given
        val exception = BlankProductIdException()
        val repository = FakeProductRepository(_getById = { Result.failure(exception) })

        val getProductByIdUseCase = GetProductByIdUseCase(repository)

        val tool = GetProductByIdTool(getProductByIdUseCase)

        // Mock the logger field using reflection to avoid log messages during tests
        val loggerField = GetProductByIdTool::class.java.getDeclaredField("logger")
        loggerField.isAccessible = true
        val mockLogger = mockk<Logger>(relaxed = true)
        loggerField.set(tool, mockLogger)

        // Create request with arguments
        val request = mockk<CallToolRequest>()
        val requestArgs = GetProductByIdRequest("test")
        val jsonArgs = Json.encodeToJsonElement(requestArgs).jsonObject
        coEvery { request.arguments } returns jsonArgs

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(true, result.isError)
        assertEquals(1, result.content.size)
        val content = result.content[0]
        assertIs<TextContent>(content)
        val text = content.text
        assertEquals("Product ID cannot be blank", text)
    }

    @Test
    fun `When execute is called and use case returns other exception then it returns error`() = runTest {
        // Given
        val exception = RuntimeException("Test error")
        val repository = FakeProductRepository(_getById = { Result.failure(exception) })
        val getProductByIdUseCase = GetProductByIdUseCase(repository)
        val tool = GetProductByIdTool(getProductByIdUseCase)

        // Create request with arguments
        val request = mockk<CallToolRequest>()
        val requestArgs = GetProductByIdRequest("test")
        val jsonArgs = Json.encodeToJsonElement(requestArgs).jsonObject
        coEvery { request.arguments } returns jsonArgs

        // When
        val result = tool.execute(request)

        // Then
        assertEquals(true, result.isError)
        assertEquals(1, result.content.size)
        val content = result.content[0]
        assertIs<TextContent>(content)
        val text = content.text
        assertEquals("Failed to retrieve product with id: test", text)
    }

    @Test
    fun `When tool is created then it has correct properties`() {
        // Given
        val getProductByIdUseCase = mockk<GetProductByIdUseCase>()

        // When
        val tool = GetProductByIdTool(getProductByIdUseCase)

        // Then
        assertEquals("get_product_by_id", tool.name)
        assertTrue(tool.description.isNotBlank())
        assertNotNull(tool.input)
        assertEquals(GetProductByIdRequest::class, tool.input)
        assertNotNull(tool.output)
        assertEquals(McpProduct::class, tool.output)
    }
}