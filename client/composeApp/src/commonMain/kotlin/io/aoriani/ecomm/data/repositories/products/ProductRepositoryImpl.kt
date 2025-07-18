package io.aoriani.ecomm.data.repositories.products

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.datasources.ProductDataSource
import kotlinx.collections.immutable.ImmutableList

class ProductRepositoryImpl(private val dataSource: ProductDataSource) : ProductRepository {

    override suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>> {
        return dataSource.fetchProducts()
    }

    override suspend fun getProduct(id: String): Result<Product?> {
        return dataSource.getProduct(id)
    }
}
