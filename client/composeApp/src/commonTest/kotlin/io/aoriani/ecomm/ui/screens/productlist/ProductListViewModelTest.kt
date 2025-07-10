package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
                id = "1",
                name = "Test Product 1",
                price = DollarAmount("10.99"),
                thumbnailUrl = "https://example.com/image1.jpg"
            ),
            ProductPreview(
                id = "2",
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
                id = "1",
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
}

private class FakeProductRepository(
    private val fetchProductsLambda: suspend () -> Result<ImmutableList<ProductPreview>> = {
        Result.success(
            persistentListOf()
        )
    },
    private val getProductLambda: suspend (String) -> Result<Product?> = { Result.success(null) }
) : ProductRepository {
    override suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>> {
        return fetchProductsLambda()
    }

    override suspend fun getProduct(id: String): Result<Product?> {
        return getProductLambda(id)
    }
}
