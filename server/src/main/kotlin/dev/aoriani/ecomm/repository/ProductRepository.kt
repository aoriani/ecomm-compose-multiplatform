package dev.aoriani.ecomm.repository

import dev.aoriani.ecomm.graphql.models.Product

/**
 * Interface for accessing product data.
 */
interface ProductRepository {
    /**
     * Retrieves all products.
     * @return A list of all [Product]s.
     */
    suspend fun getAll(): List<Product>

    /**
     * Retrieves a product by its unique identifier.
     * @param id The unique ID of the product.
     * @return The [Product] if found, otherwise null.
     */
    suspend fun getById(id: String): Product?
}
