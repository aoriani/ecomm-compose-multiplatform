package io.aoriani.ecomm.data.repositories

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList

interface ProductRepository {
    suspend fun fetchProducts(): ImmutableList<ProductPreview>
    suspend fun getProduct(id: String): Product?

    class GraphQlException(message: String, cause: Throwable? = null) : Exception(message, cause)
}