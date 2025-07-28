package io.aoriani.ecomm.domain

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.repositories.cart.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddToCartUseCaseTest {

    private class FakeCartRepository : CartRepository {
        private val addedProducts = mutableListOf<ProductBasic>()

        override val state: StateFlow<CartRepository.State>
            get() = MutableStateFlow(
                CartRepository.State(
                    items = kotlinx.collections.immutable.persistentListOf(),
                    subTotal = DollarAmount("0.0"),
                    count = 0
                )
            )

        override suspend fun add(product: ProductBasic) {
            addedProducts.add(product)
        }

        override suspend fun update(productId: String, quantity: Int) = Unit
        override suspend fun remove(productId: String) = Unit

        override suspend fun clear() = Unit
        fun getAddedProducts(): List<ProductBasic> = addedProducts
    }

    @Test
    fun `When use case is invoked then product should be added to cart repository`() = runTest {
        // Given
        val fakeCartRepository = FakeCartRepository()
        val addToCartUseCase = AddToCartUseCase(fakeCartRepository)
        val productToAdd = object : ProductBasic {
            override val id: String = "1"
            override val name: String = "Test Product"
            override val price: DollarAmount = DollarAmount("19.99")
            override val thumbnailUrl: String? = "test_image_url"
        }

        // When
        addToCartUseCase(productToAdd)

        // Then
        val addedProducts = fakeCartRepository.getAddedProducts()
        assertEquals(1, addedProducts.size, "Expected one product to be added")
        assertEquals(productToAdd, addedProducts[0], "The added product should be the one passed to the use case")
    }
}
