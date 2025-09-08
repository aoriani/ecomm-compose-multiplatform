package io.aoriani.ecomm.data.repositories.products

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.datasources.ProductDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive test suite for ProductRepositoryImpl.
 * Tests both success and failure scenarios, edge cases, and proper delegation to the data source.
 */
class ProductRepositoryImplTest {

    private lateinit var mockDataSource: FakeProductDataSource
    private lateinit var repository: ProductRepositoryImpl

    @BeforeTest
    fun setUp() {
        mockDataSource = FakeProductDataSource()
        repository = ProductRepositoryImpl(mockDataSource)
    }

    // ========== fetchProducts() Tests ==========

    @Test
    fun `When data source returns success then fetchProducts should return success`() = runTest {
        // Arrange
        val expectedProducts = persistentListOf(
            createFakeProductPreview("1", "Product 1", "10.99"),
            createFakeProductPreview("2", "Product 2", "25.50")
        )
        mockDataSource.fetchProductsResult = Result.success(expectedProducts)

        // Act
        val result = repository.fetchProducts()

        // Assert
        assertTrue(result.isSuccess, "Result should be success")
        assertEquals(expectedProducts, result.getOrNull(), "Should return expected products")
        assertTrue(mockDataSource.fetchProductsCalled, "Should call data source fetchProducts")
    }

    @Test
    fun `When data source returns empty list then fetchProducts should return empty list`() =
        runTest {
            // Arrange
            val emptyProducts = persistentListOf<ProductPreview>()
            mockDataSource.fetchProductsResult = Result.success(emptyProducts)

            // Act
            val result = repository.fetchProducts()

            // Assert
            assertTrue(result.isSuccess, "Result should be success")
            assertEquals(emptyProducts, result.getOrNull(), "Should return empty list")
            assertTrue(mockDataSource.fetchProductsCalled, "Should call data source fetchProducts")
        }

    @Test
    fun `When data source returns failure then fetchProducts should return failure`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Network error")
        mockDataSource.fetchProductsResult = Result.failure(expectedException)

        // Act
        val result = repository.fetchProducts()

        // Assert
        assertTrue(result.isFailure, "Result should be failure")
        assertEquals(
            expectedException,
            result.exceptionOrNull(),
            "Should return expected exception"
        )
        assertTrue(mockDataSource.fetchProductsCalled, "Should call data source fetchProducts")
    }

    @Test
    fun `When data source returns ProductException then fetchProducts should return ProductException`() =
        runTest {
            // Arrange
            val expectedException = ProductRepository.ProductException("Product fetch failed")
            mockDataSource.fetchProductsResult = Result.failure(expectedException)

            // Act
            val result = repository.fetchProducts()

            // Assert
            assertTrue(result.isFailure, "Result should be failure")
            assertEquals(
                expectedException,
                result.exceptionOrNull(),
                "Should return expected ProductException"
            )
            assertTrue(mockDataSource.fetchProductsCalled, "Should call data source fetchProducts")
        }

    // ========== getProduct() Tests ========== 

    @Test
    fun `When data source returns product then getProduct should return success with product`() =
        runTest {
            // Arrange
            val productId = "test-product-1"
            val expectedProduct = createFakeProduct(productId, "Test Product", "29.99")
            mockDataSource.getProductResult = Result.success(expectedProduct)

            // Act
            val result = repository.getProduct(productId)

            // Assert
            assertTrue(result.isSuccess, "Result should be success")
            assertEquals(expectedProduct, result.getOrNull(), "Should return expected product")
            assertEquals(
                productId,
                mockDataSource.getProductIdParameter,
                "Should call data source with correct ID"
            )
            assertTrue(mockDataSource.getProductCalled, "Should call data source getProduct")
        }

    @Test
    fun `When data source returns null then getProduct should return success with null`() =
        runTest {
            // Arrange
            val productId = "non-existent-product"
            mockDataSource.getProductResult = Result.success(null)

            // Act
            val result = repository.getProduct(productId)

            // Assert
            assertTrue(result.isSuccess, "Result should be success")
            assertNull(result.getOrNull(), "Should return null when product not found")
            assertEquals(
                productId,
                mockDataSource.getProductIdParameter,
                "Should call data source with correct ID"
            )
            assertTrue(mockDataSource.getProductCalled, "Should call data source getProduct")
        }

    @Test
    fun `When data source returns failure then getProduct should return failure`() = runTest {
        // Arrange
        val productId = "error-product"
        val expectedException = RuntimeException("Database error")
        mockDataSource.getProductResult = Result.failure(expectedException)

        // Act
        val result = repository.getProduct(productId)

        // Assert
        assertTrue(result.isFailure, "Result should be failure")
        assertEquals(
            expectedException,
            result.exceptionOrNull(),
            "Should return expected exception"
        )
        assertEquals(
            productId,
            mockDataSource.getProductIdParameter,
            "Should call data source with correct ID"
        )
        assertTrue(mockDataSource.getProductCalled, "Should call data source getProduct")
    }

    @Test
    fun `When data source returns ProductException then getProduct should return ProductException`() =
        runTest {
            // Arrange
            val productId = "exception-product"
            val expectedException = ProductRepository.ProductException(
                "Product not accessible",
                RuntimeException("Root cause")
            )
            mockDataSource.getProductResult = Result.failure(expectedException)

            // Act
            val result = repository.getProduct(productId)

            // Assert
            assertTrue(result.isFailure, "Result should be failure")
            assertEquals(
                expectedException,
                result.exceptionOrNull(),
                "Should return expected ProductException"
            )
            assertEquals(
                productId,
                mockDataSource.getProductIdParameter,
                "Should call data source with correct ID"
            )
            assertTrue(mockDataSource.getProductCalled, "Should call data source getProduct")
        }

    @Test
    fun `When empty string ID is provided then getProduct should handle empty string ID`() =
        runTest {
            // Arrange
            val emptyId = ""
            mockDataSource.getProductResult = Result.success(null)

            // Act
            val result = repository.getProduct(emptyId)

            // Assert
            assertTrue(result.isSuccess, "Result should be success")
            assertNull(result.getOrNull(), "Should return null for empty ID")
            assertEquals(
                emptyId,
                mockDataSource.getProductIdParameter,
                "Should pass empty ID to data source"
            )
            assertTrue(mockDataSource.getProductCalled, "Should call data source getProduct")
        }

    @Test
    fun `When special characters in ID are provided then getProduct should handle special characters in ID`() =
        runTest {
            // Arrange
            val specialId = "product-123_$%&"
            val expectedProduct = createFakeProduct(specialId, "Special Product", "15.00")
            mockDataSource.getProductResult = Result.success(expectedProduct)

            // Act
            val result = repository.getProduct(specialId)

            // Assert
            assertTrue(result.isSuccess, "Result should be success")
            assertEquals(expectedProduct, result.getOrNull(), "Should return expected product")
            assertEquals(
                specialId,
                mockDataSource.getProductIdParameter,
                "Should handle special characters in ID"
            )
            assertTrue(mockDataSource.getProductCalled, "Should call data source getProduct")
        }

    // ========== Edge Cases and Integration Tests ========== 

    @Test
    fun `When multiple consecutive calls are made then repository should work correctly`() =
        runTest {
            // Arrange
            val products = persistentListOf(createFakeProductPreview("1", "Product 1", "10.00"))
            val product = createFakeProduct("1", "Product 1", "10.00")

            mockDataSource.fetchProductsResult = Result.success(products)
            mockDataSource.getProductResult = Result.success(product)

            // Act & Assert - First call
            val fetchResult1 = repository.fetchProducts()
            assertTrue(fetchResult1.isSuccess)
            assertEquals(products, fetchResult1.getOrNull())

            // Act & Assert - Second call
            val getResult1 = repository.getProduct("1")
            assertTrue(getResult1.isSuccess)
            assertEquals(product, getResult1.getOrNull())

            // Act & Assert - Third call (fetch again)
            val fetchResult2 = repository.fetchProducts()
            assertTrue(fetchResult2.isSuccess)
            assertEquals(products, fetchResult2.getOrNull())

            // Verify calls
            assertTrue(mockDataSource.fetchProductsCalled, "Should call fetchProducts")
            assertTrue(mockDataSource.getProductCalled, "Should call getProduct")
        }

    // ========== Helper Methods ========== 

    private fun createFakeProductPreview(
        idSuffix: String,
        nameSuffix: String,
        price: String,
        thumbnailUrl: String? = "https://example.com/thumbnail$idSuffix.jpg"
    ): ProductPreview {
        return ProductPreview(
            id = ProductBasic.Id("product-id-$idSuffix"),
            name = nameSuffix,
            price = DollarAmount(price),
            thumbnailUrl = thumbnailUrl
        )
    }

    private fun createFakeProduct(
        idSuffix: String,
        nameSuffix: String,
        price: String,
        description: String = "Description for $nameSuffix",
        material: String = "Cotton",
        countryOfOrigin: String = "USA",
        inStock: Boolean = true,
        images: ImmutableList<String> = persistentListOf(
            "https://example.com/image1_$idSuffix.jpg",
            "https://example.com/image2_$idSuffix.jpg"
        )
    ): Product {
        return Product(
            id = ProductBasic.Id("product-id-$idSuffix"),
            name = nameSuffix,
            price = DollarAmount(price),
            description = description,
            material = material,
            countryOfOrigin = countryOfOrigin,
            inStock = inStock,
            images = images
        )
    }

    // ========== Fake ProductDataSource ==========

    private class FakeProductDataSource : ProductDataSource {
        var fetchProductsResult: Result<ImmutableList<ProductPreview>> =
            Result.success(persistentListOf())
        var fetchProductsCalled: Boolean = false

        var getProductResult: Result<Product?> = Result.success(null)
        var getProductCalled: Boolean = false
        var getProductIdParameter: String? = null

        override suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>> {
            fetchProductsCalled = true
            return fetchProductsResult
        }

        override suspend fun getProduct(id: String): Result<Product?> {
            getProductCalled = true
            getProductIdParameter = id
            return getProductResult
        }
    }
}
