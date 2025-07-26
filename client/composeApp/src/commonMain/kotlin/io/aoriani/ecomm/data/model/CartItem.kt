package io.aoriani.ecomm.data.model

/**
 * Represents an item in a shopping cart.
 *
 * @property id The unique identifier of the item.
 * @property name The name of the item.
 * @property unitPrice The price of a single unit of the item.
 * @property quantity The number of units of this item in the cart.
 * @property totalPrice The total price for this item (unitPrice * quantity).
 */
data class CartItem(val id: String, val name: String, val unitPrice: DollarAmount, val quantity: Int) {
    val totalPrice: DollarAmount inline get() = unitPrice * quantity
}