package io.aoriani.ecomm.data.repositories.products

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList

/**
 * Defines the contract for accessing product data.
 * This interface provides methods for fetching a list of all products and retrieving a single product by its ID.
 */
interface ProductRepository {
    /**
     * Fetches a list of all products.
     * @return A [Result] containing an [ImmutableList] of [ProductPreview] on success, or an exception on failure.
     */
    suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>>

    /**
     * Retrieves a single product by its unique identifier.
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] containing the [Product] if found, or null if no product with the given ID exists.
     * On failure, it returns an exception.
     */
    suspend fun getProduct(id: String): Result<Product?>

    /**
     * A custom exception for product-related errors.
     * This exception can be used to wrap any errors that occur during product data operations,
     * providing more specific context.
     * @param message A descriptive message for the exception.
     * @param cause The underlying cause of the exception, if any.
     */
    class ProductException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
