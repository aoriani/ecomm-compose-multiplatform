package io.aoriani.ecomm.data.repositories.products

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.datasources.ProductDataSource
import kotlinx.collections.immutable.ImmutableList

/**
 * Implementation of the [ProductRepository] interface.
 * This repository acts as a bridge between the application's data layer and the [ProductDataSource],
 * providing a concrete way to access product information.
 * @param dataSource The data source responsible for fetching product data.
 */
class ProductRepositoryImpl(private val dataSource: ProductDataSource) : ProductRepository {

    /**
     * Fetches a list of all products from the data source.
     * @return A [Result] containing an [ImmutableList] of [ProductPreview] on success, or an exception on failure.
     */
    override suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>> {
        return dataSource.fetchProducts()
    }

    /**
     * Retrieves a single product by its unique identifier from the data source.
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] containing the [Product] if found, or null if no product with the given ID exists.
     * On failure, it returns an exception.
     */
    override suspend fun getProduct(id: String): Result<Product?> {
        return dataSource.getProduct(id)
    }
}
