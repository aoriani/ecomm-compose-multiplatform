package dev.aoriani.ecomm.domain.repositories

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId

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
     * Retrieves a product by its unique identifier from the repository.
     *
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] containing the requested [Product] if found, or a failure if the product does not exist
     *         or an error occurs during retrieval.
     */
    suspend fun getById(id: ProductId): Result<Product>
}
