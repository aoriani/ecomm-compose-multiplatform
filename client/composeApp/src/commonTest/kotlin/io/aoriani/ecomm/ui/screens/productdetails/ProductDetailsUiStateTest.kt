package io.aoriani.ecomm.ui.screens.productdetails

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductBasic
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductDetailsUiStateTest {

    @Test
    fun `When Loading state is initialized then it should hold the correct title and imageUrl`() {
        // Arrange
        val title = "Loading..."
        val imageUrl = "http://example.com/placeholder.png"

        // Act
        val state = ProductDetailsUiState.Loading(title = title, imageUrl = imageUrl)

        // Assert
        assertEquals(title, state.title, "Title should match the provided value")
        assertEquals(imageUrl, state.imageUrl, "Image URL should match the provided value")
    }

    @Test
    fun `When Loading state is initialized with null imageUrl then it should hold the null value`() {
        // Arrange
        val title = "Loading..."

        // Act
        val state = ProductDetailsUiState.Loading(title = title, imageUrl = null)

        // Assert
        assertEquals(title, state.title, "Title should match the provided value")
        assertNull(state.imageUrl, "Image URL should be null")
    }

    @Test
    fun `When Loaded state is initialized with a product then title should be the product name`() {
        // Arrange
        val product = Product(
            id = ProductBasic.Id("1"),
            name = "Awesome T-Shirt",
            images = persistentListOf("image1.png"),
            price = DollarAmount("19.99"),
            description = "A cool t-shirt",
            material = "Cotton",
            countryOfOrigin = "USA",
            inStock = true
        )

        // Act
        val state = ProductDetailsUiState.Loaded(product = product)

        // Assert
        assertEquals(product.name, state.title, "Title should be derived from the product's name")
    }

    @Test
    fun `When Loaded state and product has images then imageUrl should be the first image`() {
        // Arrange
        val imageUrl = "http://example.com/product.png"
        val product = Product(
            id = ProductBasic.Id("1"),
            name = "Awesome T-Shirt",
            images = persistentListOf(imageUrl, "image2.png"),
            price = DollarAmount("19.99"),
            description = "A cool t-shirt",
            material = "Cotton",
            countryOfOrigin = "USA",
            inStock = true
        )

        // Act
        val state = ProductDetailsUiState.Loaded(product = product)

        // Assert
        assertEquals(imageUrl, state.imageUrl, "Image URL should be the first from the product's image list")
    }

    @Test
    fun `When Loaded state and product has no images then imageUrl should be an empty string`() {
        // Arrange
        val product = Product(
            id = ProductBasic.Id("1"),
            name = "Awesome T-Shirt",
            images = persistentListOf(),
            price = DollarAmount("19.99"),
            description = "A cool t-shirt",
            material = "Cotton",
            countryOfOrigin = "USA",
            inStock = true
        )

        // Act
        val state = ProductDetailsUiState.Loaded(product = product)

        // Assert
        assertEquals("", state.imageUrl, "Image URL should be an empty string when the product has no images")
    }

    @Test
    fun `When Loaded state and addToCart is called then the provided lambda should be invoked`() {
        // Arrange
        var wasAddToCartCalled = false
        val product = Product(
            id = ProductBasic.Id("1"),
            name = "Test Product",
            images = persistentListOf(),
            price = DollarAmount("1.00"),
            description = "Desc",
            material = "Cotton",
            countryOfOrigin = "USA",
            inStock = true
        )
        val state = ProductDetailsUiState.Loaded(product = product, _addToCart = { wasAddToCartCalled = true })

        // Act
        assertFalse(wasAddToCartCalled, "Pre-condition: addToCart should not have been called yet")
        state.addToCart()

        // Assert
        assertTrue(wasAddToCartCalled, "The _addToCart lambda should have been invoked")
    }

    @Test
    fun `When Error state is initialized then it should hold the correct title and imageUrl`() {
        // Arrange
        val title = "Error"
        val imageUrl = "http://example.com/error.png"

        // Act
        val state = ProductDetailsUiState.Error(title = title, imageUrl = imageUrl)

        // Assert
        assertEquals(title, state.title, "Title should match the provided value")
        assertEquals(imageUrl, state.imageUrl, "Image URL should match the provided value")
    }

    @Test
    fun `When Error state and retry is called then the provided lambda should be invoked`() {
        // Arrange
        var wasRetryCalled = false
        val state = ProductDetailsUiState.Error(
            title = "Error",
            imageUrl = null,
            _retry = { wasRetryCalled = true }
        )

        // Act
        assertFalse(wasRetryCalled, "Pre-condition: retry should not have been called yet")
        state.retry()

        // Assert
        assertTrue(wasRetryCalled, "The _retry lambda should have been invoked")
    }
}