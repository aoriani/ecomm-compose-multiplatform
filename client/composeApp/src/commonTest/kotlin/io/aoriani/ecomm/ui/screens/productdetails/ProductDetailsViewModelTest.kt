package io.aoriani.ecomm.ui.screens.productdetails

import androidx.lifecycle.SavedStateHandle
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.domain.AddToCartUseCase
import io.aoriani.ecomm.ui.navigation.Routes
import io.aoriani.ecomm.ui.test.fakes.FakeProductRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle
    private val testRoute = Routes.ProductDetails(
        id = "test-product-id",
        name = "Test Product",
        imageUrl = "https://example.com/image.jpg"
    )
    
    private val testProduct = Product(
        id = ProductBasic.Id("test-product-id"),
        name = "Loaded Product Name",
        images = persistentListOf("https://example.com/loaded-image.jpg"),
        price = DollarAmount("29.99"),
        description = "A great test product",
        material = "Cotton",
        countryOfOrigin = "USA",
        inStock = true
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        savedStateHandle["id"] = testRoute.id
        savedStateHandle["name"] = testRoute.name
        savedStateHandle["imageUrl"] = testRoute.imageUrl
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given ViewModel initialization when created then state should be Loading with route data`() = runTest {
        // Arrange
        val fakeProductRepository = FakeProductRepository()
        val addToCartUseCase = AddToCartUseCase { _ -> }

        // Act
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Assert
        val initialState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loading>(initialState)
        assertEquals(testRoute.name, initialState.title)
        assertEquals(testRoute.imageUrl, initialState.imageUrl)
    }

    @Test
    fun `Given fetchProductDetails called when product is successfully loaded then state should be Loaded`() = runTest {
        // Arrange
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { id ->
                if (id == testRoute.id) {
                    Result.success(testProduct)
                } else {
                    Result.success(null)
                }
            }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Act - Wait for the initial fetchProductDetails to complete
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loaded>(finalState)
        assertEquals(testProduct.name, finalState.title)
        assertEquals(testProduct.images.first(), finalState.imageUrl)
        assertEquals(testProduct, finalState.product)
    }

    @Test
    fun `Given fetchProductDetails called when product is null then state should be Error`() = runTest {
        // Arrange
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.success(null) }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Act - Wait for the initial fetchProductDetails to complete
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Error>(finalState)
        assertEquals(testRoute.name, finalState.title)
        assertEquals(testRoute.imageUrl, finalState.imageUrl)
    }

    @Test
    fun `Given fetchProductDetails called when repository throws exception then state should be Error`() = runTest {
        // Arrange
        val expectedException = ProductRepository.ProductException("Network error")
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.failure(expectedException) }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Act - Wait for the initial fetchProductDetails to complete
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Error>(finalState)
        assertEquals(testRoute.name, finalState.title)
        assertEquals(testRoute.imageUrl, finalState.imageUrl)
    }

    @Test
    fun `Given Error state with retry when retry is called then fetchProductDetails should be called again`() = runTest {
        // Arrange
        var fetchCallCount = 0
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { 
                fetchCallCount++
                if (fetchCallCount == 1) {
                    Result.failure(ProductRepository.ProductException("First call fails"))
                } else {
                    Result.success(testProduct)
                }
            }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Wait for initial load (should fail)
        advanceUntilIdle()
        val errorState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Error>(errorState)

        // Act - Retry
        errorState.retry()
        advanceUntilIdle()

        // Assert
        assertEquals(2, fetchCallCount, "fetchProductDetails should have been called twice")
        val finalState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loaded>(finalState)
        assertEquals(testProduct, finalState.product)
    }

    @Test
    fun `Given manual fetchProductDetails call when state is already loaded then state should transition to Loading first`() = runTest {
        // Arrange
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.success(testProduct) }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Wait for initial load
        advanceUntilIdle()
        assertIs<ProductDetailsUiState.Loaded>(viewModel.state.first())

        // Act - Manually call fetchProductDetails
        viewModel.fetchProductDetails()

        // Assert - Should transition to Loading first
        val loadingState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loading>(loadingState)
        assertEquals(testRoute.name, loadingState.title)
        assertEquals(testRoute.imageUrl, loadingState.imageUrl)

        // Complete the operation
        advanceUntilIdle()
        assertIs<ProductDetailsUiState.Loaded>(viewModel.state.first())
    }

    @Test
    fun `Given Loaded state when addToCart is called then addToCartUseCase should be invoked with product`() = runTest {
        // Arrange
        var addToCartCallCount = 0
        var capturedProduct: ProductBasic? = null
        val addToCartUseCase = AddToCartUseCase { product ->
            addToCartCallCount++
            capturedProduct = product
        }
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.success(testProduct) }
        )
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Wait for initial load
        advanceUntilIdle()
        val loadedState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loaded>(loadedState)

        // Act
        viewModel.addToCart()
        advanceUntilIdle()

        // Assert
        assertEquals(1, addToCartCallCount, "addToCartUseCase should have been called once")
        assertEquals(testProduct, capturedProduct, "addToCartUseCase should have been called with the loaded product")
    }

    @Test
    fun `Given Loaded state when addToCart is called via UI state then addToCartUseCase should be invoked`() = runTest {
        // Arrange
        var addToCartCallCount = 0
        val addToCartUseCase = AddToCartUseCase { addToCartCallCount++ }
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.success(testProduct) }
        )
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Wait for initial load
        advanceUntilIdle()
        val loadedState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loaded>(loadedState)

        // Act
        loadedState.addToCart()
        advanceUntilIdle()

        // Assert
        assertEquals(1, addToCartCallCount, "addToCartUseCase should have been called once")
    }

    @Test
    fun `Given Loading state when addToCart is called then it should throw IllegalStateException`() = runTest {
        // Arrange
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { 
                // Return a result that keeps the state in Loading (simulate slow network)
                Result.success(testProduct)
            }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Don't advance the scheduler, so state remains Loading
        val loadingState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loading>(loadingState)

        // Act & Assert
        assertFailsWith<IllegalStateException> {
            viewModel.addToCart()
        }
    }

    @Test
    fun `Given Error state when addToCart is called then it should throw IllegalStateException`() = runTest {
        // Arrange
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.failure(ProductRepository.ProductException("Error")) }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Wait for initial load to fail
        advanceUntilIdle()
        val errorState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Error>(errorState)

        // Act & Assert
        assertFailsWith<IllegalStateException> {
            viewModel.addToCart()
        }
    }

    @Test
    fun `Given product with multiple images when loaded then imageUrl should be first image`() = runTest {
        // Arrange
        val productWithMultipleImages = testProduct.copy(
            images = persistentListOf(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg"
            )
        )
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.success(productWithMultipleImages) }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Act
        advanceUntilIdle()

        // Assert
        val loadedState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loaded>(loadedState)
        assertEquals("https://example.com/image1.jpg", loadedState.imageUrl)
    }

    @Test
    fun `Given product with no images when loaded then imageUrl should be empty string`() = runTest {
        // Arrange
        val productWithNoImages = testProduct.copy(images = persistentListOf())
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.success(productWithNoImages) }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Act
        advanceUntilIdle()

        // Assert
        val loadedState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Loaded>(loadedState)
        assertEquals("", loadedState.imageUrl)
    }

    @Test
    fun `Given route with null imageUrl when error occurs then error state should have null imageUrl`() = runTest {
        // Arrange
        savedStateHandle["imageUrl"] = null
        val fakeProductRepository = FakeProductRepository(
            getProductLambda = { Result.failure(ProductRepository.ProductException("Error")) }
        )
        val addToCartUseCase = AddToCartUseCase { _ -> }
        val viewModel = ProductDetailsViewModel(
            productRepository = fakeProductRepository,
            addToCartUseCase = addToCartUseCase,
            savedStateHandle = savedStateHandle
        )

        // Act
        advanceUntilIdle()

        // Assert
        val errorState = viewModel.state.first()
        assertIs<ProductDetailsUiState.Error>(errorState)
        assertEquals(null, errorState.imageUrl)
    }
}