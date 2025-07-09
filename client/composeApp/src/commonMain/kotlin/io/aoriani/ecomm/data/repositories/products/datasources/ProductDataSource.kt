package io.aoriani.ecomm.data.repositories.products.datasources

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList

interface ProductDataSource {
    suspend fun fetchProducts(): ImmutableList<ProductPreview>
    suspend fun getProduct(id: String): Product?
}