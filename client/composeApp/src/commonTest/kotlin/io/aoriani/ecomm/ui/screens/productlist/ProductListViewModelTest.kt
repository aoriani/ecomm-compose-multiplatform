package io.aoriani.ecomm.ui.screens.productlist

import co.touchlab.kermit.ExperimentalKermitApi
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalKermitApi::class)
class ProductListViewModelTest {

    @Test
    fun `Verify that initial state of ProductListViewModel is Loading`() = runTest {
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