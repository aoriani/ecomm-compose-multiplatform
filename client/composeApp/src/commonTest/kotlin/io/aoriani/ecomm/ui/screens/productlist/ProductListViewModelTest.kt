package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.model.ZERO
import io.aoriani.ecomm.data.repositories.cart.CartRepository
import io.aoriani.ecomm.ui.test.fakes.FakeCartRepository
import io.aoriani.ecomm.ui.test.fakes.FakeProductRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    @Test
    fun `When ProductListViewModel is initialized then initial state is Loading`() = runTest {
        val fakeProductRepository = FakeProductRepository(fetchProductsLambda = {
            delay(1000)
            return@FakeProductRepository Result.success(persistentListOf())
        })
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = ProductListViewModel(
            productRepository = fakeProductRepository,
            cartRepository = FakeCartRepository(),
            addToCartUseCase = {},
            dispatcher = testDispatcher
        )
        val state = viewModel.state.value
        assertIs<ProductListUiState.Loading>(state)
    }

    @Test
    fun `When products are fetched successfully then state is Success with products`() = runTest {
        // Arrange
        val mockProducts = persistentListOf(
            ProductPreview(
                id = ProductBasic.Id("1"),
                name = "Test Product 1",
                price = DollarAmount("10.99"),
                thumbnailUrl = "https://example.com/image1.jpg"
            ), ProductPreview(
                id = ProductBasic.Id("2"),
                name = "Test Product 2",
                price = DollarAmount("20.99"),
                thumbnailUrl = "https://example.com/image2.jpg"
            )
        )

        val fakeProductRepository = FakeProductRepository(fetchProductsLambda = {
            delay(100)
            Result.success(mockProducts)
        })

        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Act
        val viewModel = ProductListViewModel(
            productRepository = fakeProductRepository,
            cartRepository = FakeCartRepository(),
            addToCartUseCase = { },
            dispatcher = testDispatcher
        )

        // Initial state should be Loading
        assertIs<ProductListUiState.Loading>(viewModel.state.value)

        // Advance time to complete the coroutine
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertIs<ProductListUiState.Success>(finalState)
        assertEquals(mockProducts, finalState.products)
    }

    @Test
    fun `When product fetching fails then state is Error`() = runTest {
        // Arrange
        val testException = RuntimeException("Network error")
        val fakeProductRepository = FakeProductRepository(fetchProductsLambda = {
            delay(100)
            Result.failure(testException)
        })

        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Act
        val viewModel = ProductListViewModel(
            productRepository = fakeProductRepository,
            cartRepository = FakeCartRepository(),
            addToCartUseCase = { },
            dispatcher = testDispatcher
        )

        // Initial state should be Loading
        assertIs<ProductListUiState.Loading>(viewModel.state.value)

        // Advance time to complete the coroutine
        advanceUntilIdle()

        // Assert
        assertIs<ProductListUiState.Error>(viewModel.state.value)
    }

    @Test
    fun `When products are fetched then state transitions from Loading to Success`() = runTest {
        // Arrange
        val mockProducts = persistentListOf(
            ProductPreview(
                id = ProductBasic.Id("1"),
                name = "Test Product",
                price = DollarAmount("15.99"),
                thumbnailUrl = "https://example.com/image.jpg"
            )
        )

        val fakeProductRepository = FakeProductRepository(fetchProductsLambda = {
            delay(500)
            Result.success(mockProducts)
        })

        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Act
        val viewModel = ProductListViewModel(
            productRepository = fakeProductRepository,
            cartRepository = FakeCartRepository(),
            addToCartUseCase = { },
            dispatcher = testDispatcher
        )

        // Assert initial state
        assertIs<ProductListUiState.Loading>(viewModel.state.value)

        // Advance time partially
        advanceTimeBy(100)
        assertIs<ProductListUiState.Loading>(viewModel.state.value)

        // Advance to completion
        advanceTimeBy(500)
        assertIs<ProductListUiState.Success>(viewModel.state.value)
        assertEquals(mockProducts, (viewModel.state.value as ProductListUiState.Success).products)
    }

    @Test
    fun `When products fail to fetch then state transitions from Loading to Error`() = runTest {
        // Arrange
        val mockProducts = persistentListOf(
            ProductPreview(
                id = ProductBasic.Id("1"),
                name = "Test Product",
                price = DollarAmount("15.99"),
                thumbnailUrl = "https://example.com/image.jpg"
            )
        )

        val fakeProductRepository = FakeProductRepository(fetchProductsLambda = {
            delay(500)
            Result.failure(RuntimeException("Network error"))
        })

        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Act
        val viewModel = ProductListViewModel(
            productRepository = fakeProductRepository,
            cartRepository = FakeCartRepository(),
            addToCartUseCase = { },
            dispatcher = testDispatcher
        )

        // Assert initial state
        assertIs<ProductListUiState.Loading>(viewModel.state.value)

        // Advance time partially
        advanceTimeBy(100)
        assertIs<ProductListUiState.Loading>(viewModel.state.value)

        // Advance to completion
        advanceTimeBy(500)
        assertIs<ProductListUiState.Error>(viewModel.state.value)
    }

    @Test
    fun `When addToCart is called then the cart is updated`() = runTest {
        var wasAddToCartCalled = false
        var productToAdd: ProductBasic? = null
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(),
            cartRepository = FakeCartRepository(),
            addToCartUseCase = {
                wasAddToCartCalled = true
                productToAdd = it
            },
            dispatcher = testDispatcher
        )

        val fakeProduct = object : ProductBasic {
            override val id: ProductBasic.Id = ProductBasic.Id("1")
            override val name: String = "Test Product"
            override val price: DollarAmount = DollarAmount("10.99")
            override val thumbnailUrl: String = "https://example.com/image.jpg"
        }
        viewModel.addToCart(fakeProduct)
        advanceUntilIdle()
        assertEquals(true, wasAddToCartCalled)
        assertEquals(fakeProduct, productToAdd)
    }

    @Test
    fun `When state is loading and cart items change then state is updated`() = runTest {
        val fakeProductRepository = FakeProductRepository(fetchProductsLambda = {
            // Never returns a result
            awaitCancellation()
        })
        val cartState =
            MutableStateFlow(CartRepository.State(persistentListOf(), DollarAmount.ZERO, 0))
        val fakeCartRepository = FakeCartRepository(_state = cartState)
        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Act
        val viewModel = ProductListViewModel(
            productRepository = fakeProductRepository,
            cartRepository = fakeCartRepository,
            addToCartUseCase = { },
            dispatcher = testDispatcher
        )

        advanceUntilIdle()

        assertIs<ProductListUiState.Loading>(viewModel.state.value)
        assertEquals(0, (viewModel.state.value as ProductListUiState.Error).cartItemCount)

        cartState.update { it.copy(count = 3) }

        advanceUntilIdle()

        assertIs<ProductListUiState.Loading>(viewModel.state.value)
        assertEquals(3, (viewModel.state.value as ProductListUiState.Error).cartItemCount)
    }

    @Test
    fun `When state is success and cart items change then state is updated`() = runTest {
        val cartState =
            MutableStateFlow(CartRepository.State(persistentListOf(), DollarAmount.ZERO, 0))
        val fakeCartRepository = FakeCartRepository(_state = cartState)
        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Act
        val viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(),
            cartRepository = fakeCartRepository,
            addToCartUseCase = { },
            dispatcher = testDispatcher
        )

        advanceUntilIdle()

        assertIs<ProductListUiState.Success>(viewModel.state.value)
        assertEquals(0, (viewModel.state.value as ProductListUiState.Error).cartItemCount)

        cartState.update { it.copy(count = 3) }

        advanceUntilIdle()

        assertIs<ProductListUiState.Success>(viewModel.state.value)
        assertEquals(3, (viewModel.state.value as ProductListUiState.Error).cartItemCount)
    }

    @Test
    fun `When state is error and cart items change then state is updated`() = runTest {
        val fakeProductRepository = FakeProductRepository(fetchProductsLambda = {
            Result.failure(RuntimeException("Network error"))
        })
        val cartState =
            MutableStateFlow(CartRepository.State(persistentListOf(), DollarAmount.ZERO, 0))
        val fakeCartRepository = FakeCartRepository(_state = cartState)
        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Act
        val viewModel = ProductListViewModel(
            productRepository = fakeProductRepository,
            cartRepository = fakeCartRepository,
            addToCartUseCase = { },
            dispatcher = testDispatcher
        )

        advanceUntilIdle()

        assertIs<ProductListUiState.Error>(viewModel.state.value)
        assertEquals(0, (viewModel.state.value as ProductListUiState.Error).cartItemCount)

        cartState.update { it.copy(count = 3) }

        advanceUntilIdle()

        assertIs<ProductListUiState.Error>(viewModel.state.value)
        assertEquals(3, (viewModel.state.value as ProductListUiState.Error).cartItemCount)
    }

}



