package dev.aoriani.ecomm.presentation.mcp.models

import dev.aoriani.ecomm.domain.models.Product as DomainProduct
import dev.aoriani.ecomm.domain.models.ProductId
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductTest {

    @Test
    fun `When toMcpProduct is called then it correctly maps domain product to MCP product`() {
        // Given
        val domainProduct = DomainProduct(
            id = ProductId("test1"),
            name = "Test Product",
            price = BigDecimal("19.99"),
            description = "Test description",
            images = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
            material = "Test material",
            inStock = true,
            countryOfOrigin = "Test Country"
        )
        
        // When
        val mcpProduct = domainProduct.toMcpProduct()
        
        // Then
        assertEquals("test1", mcpProduct.id)
        assertEquals("Test Product", mcpProduct.name)
        assertEquals(BigDecimal("19.99"), mcpProduct.price)
        assertEquals("Test description", mcpProduct.description)
        assertEquals(listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"), mcpProduct.images)
        assertEquals("Test material", mcpProduct.material)
        assertEquals(true, mcpProduct.inStock)
        assertEquals("Test Country", mcpProduct.countryOfOrigin)
    }
    
    @Test
    fun `When Product is serialized then it produces valid JSON`() {
        // Given
        val product = Product(
            id = "test1",
            name = "Test Product",
            price = BigDecimal("19.99"),
            description = "Test description",
            images = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
            material = "Test material",
            inStock = true,
            countryOfOrigin = "Test Country"
        )
        
        // When
        val json = Json.encodeToString(product)
        
        // Then
        val expectedJson = """{"id":"test1","name":"Test Product","price":19.99,"description":"Test description","images":["https://example.com/image1.jpg","https://example.com/image2.jpg"],"material":"Test material","inStock":true,"countryOfOrigin":"Test Country"}"""
        assertEquals(expectedJson, json)
    }
    
    @Test
    fun `When Products is serialized then it produces valid JSON with array`() {
        // Given
        val product = Product(
            id = "test1",
            name = "Test Product",
            price = BigDecimal("19.99"),
            description = "Test description",
            images = listOf("https://example.com/image1.jpg"),
            material = "Test material",
            inStock = true,
            countryOfOrigin = "Test Country"
        )
        
        val products = Products(listOf(product))
        
        // When
        val json = Json.encodeToString(products)
        
        // Then
        val expectedJson = """{"products":[{"id":"test1","name":"Test Product","price":19.99,"description":"Test description","images":["https://example.com/image1.jpg"],"material":"Test material","inStock":true,"countryOfOrigin":"Test Country"}]}"""
        assertEquals(expectedJson, json)
    }
    
    @Test
    fun `When GetProductByIdRequest is serialized then it produces valid JSON`() {
        // Given
        val request = GetProductByIdRequest("test1")
        
        // When
        val json = Json.encodeToString(request)
        
        // Then
        val expectedJson = """{"id":"test1"}"""
        assertEquals(expectedJson, json)
    }
}