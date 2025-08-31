package io.aoriani.ecomm.ui.test.fakes

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ZERO
import io.aoriani.ecomm.data.repositories.cart.CartRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeCartRepository(
    private val _state: StateFlow<CartRepository.State> = MutableStateFlow(
        CartRepository.State(
            persistentListOf(),
            DollarAmount.ZERO, 0
        )
    ),
    private val _add: suspend (product: ProductBasic) -> Unit = {},

) : CartRepository {
    override val state: StateFlow<CartRepository.State> get() = _state
    override suspend fun add(product: ProductBasic) = _add(product)

    override suspend fun updateQuantity(
        productId: ProductBasic.Id,
        quantity: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(productId: ProductBasic.Id) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

}