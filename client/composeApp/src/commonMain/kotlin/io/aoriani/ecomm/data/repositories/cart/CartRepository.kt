package io.aoriani.ecomm.data.repositories.cart

import io.aoriani.ecomm.data.model.CartItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val items: StateFlow<ImmutableList<CartItem>>

    fun add(id: String)
    fun update(id: String, quantity: Int)

    fun remove(id: String)
}