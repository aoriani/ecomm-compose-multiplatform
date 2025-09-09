package io.aoriani.ecomm.data.repositories.products.datasources

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList

/**
 * Defines the contract for accessing product data from a specific source.
 * This interface provides methods for fetching a list of all products and retrieving a single product by its ID.
 */
interface ProductDataSource {
    /**
     * Fetches a list of all products from the data source.
     * @return A [Result] containing an [ImmutableList] of [ProductPreview] on success, or an exception on failure.
     */
    suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>>

    /**
     * Retrieves a single product by its unique identifier from the data source.
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] containing the [Product] if found, or null if no product with the given ID exists.
     * On failure, it returns an exception.
     */
    suspend fun getProduct(id: String): Result<Product?>
}
