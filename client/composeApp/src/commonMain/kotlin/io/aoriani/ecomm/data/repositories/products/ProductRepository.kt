package io.aoriani.ecomm.data.repositories.products

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList

interface ProductRepository {
    suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>>
    suspend fun getProduct(id: String): Result<Product?>

    // Consider if ProductException is still needed or if Throwable is enough
    // For now, it can be kept if it provides specific context.
    class ProductException(message: String, cause: Throwable? = null) : Exception(message, cause)
}