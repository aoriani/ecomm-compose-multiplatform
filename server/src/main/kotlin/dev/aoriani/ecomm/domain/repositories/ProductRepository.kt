package dev.aoriani.ecomm.domain.repositories

import dev.aoriani.ecomm.domain.models.Product

/**
 * Repository abstraction for performing CRUD operations on [Product] entities.
 *
 * Implementations may use various persistence mechanisms such as
 * relational databases, NoSQL stores, or in-memory caches.
 * All methods return a Kotlin [Result] to encapsulate success or failure
 * without throwing exceptions to the caller.
 */
interface ProductRepository {
    /**
     * Retrieves all products from the underlying store.
     *
     * @return A [Result] containing a list of all [Product]s on success,
     * or a failure if an error occurs during retrieval.
     */
    suspend fun getAll(): Result<List<Product>>

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id The unique ID of the product.
     * @return A [Result] containing the [Product] if found,
     * or `null` wrapped in a successful result if no product matches the given ID.
     * The result is a failure if an error occurs during lookup.
     */
    suspend fun getById(id: String): Result<Product?>
}
