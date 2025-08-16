package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeProductRepositoryTest {

    @Test
    fun `When created with default parameters then getAll returns empty list`() = runTest {
        // Given
        val repository = FakeProductRepository()
        
        // When
        val result = repository.getAll()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList(), result.getOrThrow())
    }
    
    @Test
    fun `When created with default parameters then getById returns ProductNotFoundException`() = runTest {
        // Given
        val repository = FakeProductRepository()
        val productId = ProductId("test-id")
        
        // When
        val result = repository.getById(productId)
        
        // Then
        assertTrue(result.isFailure)
        assertIs<ProductNotFoundException>(result.exceptionOrNull())
        assertEquals("Product with id 'test-id' not found.", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `When created with custom getAll lambda then it uses that behavior`() = runTest {
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
        
        val repository = FakeProductRepository(
            _getAll = { Result.success(mockProducts) }
        )
        
        // When
        val result = repository.getAll()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockProducts, result.getOrThrow())
        assertEquals(2, result.getOrThrow().size)
    }
    
    @Test
    fun `When created with custom getById lambda then it uses that behavior`() = runTest {
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
        
        val repository = FakeProductRepository(
            _getById = { id -> 
                if (id == productId) {
                    Result.success(mockProduct)
                } else {
                    Result.failure(ProductNotFoundException(id))
                }
            }
        )
        
        // When
        val result = repository.getById(productId)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockProduct, result.getOrThrow())
    }
    
    @Test
    fun `When getById is called with non-matching ID then it returns failure`() = runTest {
        // Given
        val productId = ProductId("test1")
        val nonExistentId = ProductId("non-existent")
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
        
        val repository = FakeProductRepository(
            _getById = { id -> 
                if (id == productId) {
                    Result.success(mockProduct)
                } else {
                    Result.failure(ProductNotFoundException(id))
                }
            }
        )
        
        // When
        val result = repository.getById(nonExistentId)
        
        // Then
        assertTrue(result.isFailure)
        assertIs<ProductNotFoundException>(result.exceptionOrNull())
        assertEquals("Product with id 'non-existent' not found.", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `When getAll is configured to return error then it returns that error`() = runTest {
        // Given
        val testException = RuntimeException("Test error")
        val repository = FakeProductRepository(
            _getAll = { Result.failure(testException) }
        )
        
        // When
        val result = repository.getAll()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(testException, result.exceptionOrNull())
        assertEquals("Test error", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `When getById is configured to return custom error then it returns that error`() = runTest {
        // Given
        val testException = RuntimeException("Custom error")
        val repository = FakeProductRepository(
            _getById = { Result.failure(testException) }
        )
        
        // When
        val result = repository.getById(ProductId("any-id"))
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(testException, result.exceptionOrNull())
        assertEquals("Custom error", result.exceptionOrNull()?.message)
    }
}