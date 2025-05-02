package io.aoriani.ecomm.data.repositories

import io.aoriani.ecomm.data.model.Product
import kotlinx.collections.immutable.ImmutableList

interface ProductRepository {
    suspend fun fetchProducts(): ImmutableList<Product>
    suspend fun getProduct(id: String): Product?

    class GraphQlException(message: String, cause: Throwable? = null) : Exception(message, cause)
}