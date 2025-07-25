package io.aoriani.ecomm.data.repositories.cart

import io.aoriani.ecomm.data.model.CartItem
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ZERO
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartRepositoryImpl : CartRepository {
    private val _state = MutableStateFlow(
        CartRepository.State(
            items = persistentListOf(),
            subTotal = DollarAmount.ZERO,
            count = 0
        )
    )
    override val state: StateFlow<CartRepository.State>
        get() = _state.asStateFlow()

    private val items = ObservableLinkedHashMap<ProductBasic.Id, CartItem>().apply {
        onChangeListener = object : ObservableLinkedHashMap.OnChangeListener<CartItem> {
            override fun onChange(values: List<CartItem>) {
                _state.update {
                    CartRepository.State(
                        items = values.toPersistentList(),
                        subTotal = values.fold(DollarAmount.ZERO) { acc, item -> acc + item.totalPrice },
                        count = values.sumOf { it.quantity }
                    )
                }
            }
        }
    }

    override fun add(product: ProductBasic) {
        if (product.id in items) {
            items[product.id] =
                items.getValue(product.id).let { it.copy(quantity = it.quantity + 1) }
        } else {
            val cartItem = product.toCartItem()
            items[product.id] = cartItem
        }
    }

    override fun update(productId: ProductBasic.Id, quantity: Int) {
        if (productId in items) {
            if (quantity == 0) {
                items.remove(productId)
            } else {
                items[productId] = items.getValue(productId).copy(quantity = quantity)
            }
        }
    }

    override fun remove(productId: ProductBasic.Id) {
        if (productId in items) {
            items.remove(productId)
        }
    }

    override fun clear() {
        items.clear()
    }
}

fun ProductBasic.toCartItem(quantity: Int = 1) = CartItem(id, name, price, quantity)