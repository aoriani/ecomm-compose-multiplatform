package io.aoriani.ecomm.data.model

data class CartItem(val id: String, val name: String, val price: Double, val quantity: Int) {
    val totalPrice: Double inline get() = price * quantity
}