package io.aoriani.ecomm.data.repositories.products

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class MemProductDataSourceTest {

    // Test data
    private val testProductId = "test-product-id"
    private val testProductName = "Test Product"
    private val testProductPrice = DollarAmount("10.99")
    private val testProductDescription = "Test product description"
    private val testProductMaterial = "Test material"
    private val testProductCountryOfOrigin = "Test Country"
    private val testProductInStock = true
    private val testProductImages = persistentListOf("image1.jpg", "image2.jpg")

    private val testProduct = Product(
        id = testProductId,
        name = testProductName,
        price = testProductPrice,
        description = testProductDescription,
        material = testProductMaterial,
        countryOfOrigin = testProductCountryOfOrigin,
        inStock = testProductInStock,
        images = testProductImages
    )

    private val testProductPreview = ProductPreview(
        id = testProductId,
        name = testProductName,
        price = testProductPrice,
        thumbnailUrl = "thumbnail.jpg"
    )

    private val testProductPreviews = listOf(testProductPreview)

    // Fixed time for testing
    private val startTime = Instant.fromEpochMilliseconds(1000)
    private var currentTime = startTime

    // Mock clock that returns the current test time
    // Use a function reference instead of a lambda to ensure it always gets the current value
    private fun getCurrentTime(): Instant = currentTime

    // Create a new data source for each test
    private fun createDataSource(): MemProductDataSource {
        currentTime = startTime // Reset time
        return MemProductDataSource(nowProvider = ::getCurrentTime)
    }

    // Helper to advance time
    private fun advanceTime(minutes: Long) {
        currentTime = currentTime.plus(minutes.minutes)
    }

    @Test
    fun `getProductPreviews returns null when cache is empty`() {
        // Arrange
        val dataSource = createDataSource()

        // Act
        val result = dataSource.getProductPreviews()

        // Assert
        assertNull(result, "Should return null when no product previews are cached")
    }

    @Test
    fun `getProductPreviews returns cached value when available`() {
        // Arrange
        val dataSource = createDataSource()
        dataSource.cache(testProductPreviews)

        // Act
        val result = dataSource.getProductPreviews()

        // Assert
        assertEquals(testProductPreviews, result?.value, "Should return cached product previews")
    }

    @Test
    fun `getProduct returns null when cache is empty`() {
        // Arrange
        val dataSource = createDataSource()

        // Act
        val result = dataSource.getProduct(testProductId)

        // Assert
        assertNull(result, "Should return null when no product is cached")
    }

    @Test
    fun `getProduct returns cached value when available`() {
        // Arrange
        val dataSource = createDataSource()
        dataSource.cache(testProduct)

        // Act
        val result = dataSource.getProduct(testProductId)

        // Assert
        assertEquals(testProduct, result?.value, "Should return cached product")
    }

    @Test
    fun `cache product previews stores value correctly`() {
        // Arrange
        val dataSource = createDataSource()

        // Act
        dataSource.cache(testProductPreviews)
        val result = dataSource.getProductPreviews()

        // Assert
        assertEquals(testProductPreviews, result?.value, "Should store product previews correctly")
        // We can't directly test creationTime as it's private, but we can verify it's not expired
        assertFalse(result?.isExpired() ?: true, "Newly cached item should not be expired")
    }

    @Test
    fun `cache product stores value correctly`() {
        // Arrange
        val dataSource = createDataSource()

        // Act
        dataSource.cache(testProduct)
        val result = dataSource.getProduct(testProductId)

        // Assert
        assertEquals(testProduct, result?.value, "Should store product correctly")
        // We can't directly test creationTime as it's private, but we can verify it's not expired
        assertFalse(result?.isExpired() ?: true, "Newly cached item should not be expired")
    }

    @Test
    fun `trim removes expired product previews`() {
        // Arrange
        val dataSource = createDataSource()
        dataSource.cache(testProductPreviews)

        // Act - advance time beyond TTL (1 minute)
        advanceTime(2) // 2 minutes later
        dataSource.trim()

        // Assert
        assertNull(dataSource.getProductPreviews(), "Should remove expired product previews")
    }

    @Test
    fun `trim keeps non-expired product previews`() {
        // Arrange
        val dataSource = createDataSource()
        dataSource.cache(testProductPreviews)

        // Act - advance time but stay within TTL
        advanceTime(0) // Same time
        dataSource.trim()

        // Assert
        val result = dataSource.getProductPreviews()
        assertEquals(testProductPreviews, result?.value, "Should keep non-expired product previews")
    }

    @Test
    fun `trim removes expired products`() {
        // Arrange
        val dataSource = createDataSource()
        dataSource.cache(testProduct)

        // Act - advance time beyond TTL (1 minute)
        advanceTime(2) // 2 minutes later
        dataSource.trim()

        // Assert
        assertNull(dataSource.getProduct(testProductId), "Should remove expired products")
    }

    @Test
    fun `trim keeps non-expired products`() {
        // Arrange
        val dataSource = createDataSource()
        dataSource.cache(testProduct)

        // Act - advance time but stay within TTL
        advanceTime(0) // Same time
        dataSource.trim()

        // Assert
        val result = dataSource.getProduct(testProductId)
        assertEquals(testProduct, result?.value, "Should keep non-expired products")
    }

    @Test
    fun `trim handles mixed expired and non-expired products`() {
        // Arrange
        val dataSource = createDataSource()

        // Cache first product
        dataSource.cache(testProduct)

        // Cache second product after some time
        val secondProductId = "second-product-id"
        val secondProduct = testProduct.copy(id = secondProductId)
        advanceTime(1) // 1 minute later (at the TTL boundary)
        dataSource.cache(secondProduct)

        // Act - advance time to expire only the first product
        advanceTime(1) // 2 minutes after start, 1 minute after second product
        dataSource.trim()

        // Assert
        assertNull(dataSource.getProduct(testProductId), "Should remove expired product")
        val secondResult = dataSource.getProduct(secondProductId)
        assertEquals(secondProduct, secondResult?.value, "Should keep non-expired product")
    }
}
