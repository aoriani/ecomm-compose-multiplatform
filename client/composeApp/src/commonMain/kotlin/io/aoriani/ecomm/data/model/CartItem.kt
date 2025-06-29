package io.aoriani.ecomm.data.model

data class CartItem(val id: String, val name: String, val price: DollarAmount, val quantity: Int) {
    val totalPrice: DollarAmount inline get() = price * quantity
}