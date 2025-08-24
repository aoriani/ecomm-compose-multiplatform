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
    /**
     * Represents the current state of the shopping cart.
     * @property items The immutable list of [CartItem]s currently in the cart.
     * @property subTotal The total monetary value of all items in the cart.
     * @property count The total number of individual items in the cart (sum of quantities).
     */
    data class State(
        val items: ImmutableList<CartItem>,
        val subTotal: DollarAmount,
        val count: Int
    )

    /**
     * A [StateFlow] that emits the current [State] of the shopping cart.
     * Observers can collect this flow to receive updates whenever the cart changes.
     */
    val state: StateFlow<State>

    /**
     * Adds a product to the shopping cart.
     * If the product is already in the cart, its quantity might be incremented,
     * or a new separate item might be added, depending on the implementation.
     * @param product The [ProductBasic] to add to the cart.
     */
    suspend fun add(product: ProductBasic)

    /**
     * Updates the quantity of a specific product in the shopping cart.
     * If the quantity is set to 0 or less, the item might be removed from the cart.
     * @param productId The ID of the [ProductBasic] whose quantity is to be updated.
     * @param quantity The new quantity for the product.
     */
    suspend fun updateQuantity(productId: ProductBasic.Id, quantity: Int)

    /**
     * Removes a product completely from the shopping cart.
     * @param productId The ID of the [ProductBasic] to remove.
     */
    suspend fun remove(productId: ProductBasic.Id)

    /**
     * Clears the shopping cart, removing all items.
     */
    suspend fun clear()
}