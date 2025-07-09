package io.aoriani.ecomm.data.repositories

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.datasources.ProductDataSource
import kotlinx.collections.immutable.ImmutableList

class ProductRepositoryImpl(private val dataSource: ProductDataSource) : ProductRepository {

    override suspend fun fetchProducts(): ImmutableList<ProductPreview> {
        return dataSource.fetchProducts()
    }

    override suspend fun getProduct(id: String): Product? {
        return dataSource.getProduct(id)
    }
}

