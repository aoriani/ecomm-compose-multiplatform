package io.aoriani.ecomm.data.repositories.cart

import io.aoriani.ecomm.data.model.CartItem
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * Represents the shopping cart and its operations.
 *
 * This interface defines the contract for managing the shopping cart's state and actions.
 * It allows adding, updating, and removing items from the cart, and provides access
 * to the current state of the cart as a [StateFlow].
 */
interface CartRepository {
    data class State(
        val items: ImmutableList<CartItem>,
        val subTotal: DollarAmount,
        val count: Int
    )

    val state: StateFlow<State>

    fun add(product: ProductBasic)
    fun update(productId: ProductBasic.Id, quantity: Int)
    fun remove(productId: ProductBasic.Id)

    fun clear()
}