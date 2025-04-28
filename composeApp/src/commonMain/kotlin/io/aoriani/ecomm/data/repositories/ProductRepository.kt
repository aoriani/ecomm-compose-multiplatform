package io.aoriani.ecomm.data.repositories

import io.aoriani.ecomm.data.model.Product
import kotlinx.collections.immutable.ImmutableList

interface ProductRepository {
    suspend fun listProducts(): ImmutableList<Product>
    suspend fun getProduct(id: String): Product?
}