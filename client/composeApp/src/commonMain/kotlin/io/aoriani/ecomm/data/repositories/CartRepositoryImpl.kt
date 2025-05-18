package io.aoriani.ecomm.data.repositories

import io.aoriani.ecomm.data.model.CartItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartRepositoryImpl : CartRepository {
    override val items: StateFlow<ImmutableList<CartItem>>
        field = MutableStateFlow(persistentListOf())

    override fun add(id: String) {
        TODO("Not yet implemented")
    }

    override fun update(id: String, quantity: Int) {
        TODO("Not yet implemented")
    }

    override fun remove(id: String) {
        TODO("Not yet implemented")
    }
}