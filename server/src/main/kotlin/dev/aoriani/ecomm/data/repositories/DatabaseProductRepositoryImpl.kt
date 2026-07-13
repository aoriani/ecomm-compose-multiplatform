package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.data.database.ProductEntity
import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

/**
 * Exposed-based implementation of the [ProductRepository].
 * Uses [ProductEntity] and [dev.aoriani.ecomm.data.database.ProductTable] to interact with the database.
 */
internal class DatabaseProductRepositoryImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ProductRepository {
    /**
     * Retrieves all products from the database.
     * Operations run in an Exposed suspend transaction on [ioDispatcher].
     * @return A [Result] wrapping a list of all [Product]s. Success case contains the list
     * of products, failure case contains any database exceptions that occurred.
     */
    override suspend fun getAll(): Result<List<Product>> = runCatching {
        withContext(ioDispatcher) {
            suspendTransaction(db = database) {
                ProductEntity.all().map(ProductEntity::toProduct)
            }
        }
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * This function fetches the product from the database using the provided ID.
     * If the product is not found, a [ProductNotFoundException] is thrown.
     *
     * @param id The unique identifier of the product to retrieve. Must not be blank.
     * @return A [Result] containing the retrieved [Product] if successful, or an exception if not.
     * The result captures any errors that occur during the process, including validation or database-related issues.
     */
    override suspend fun getById(id: ProductId): Result<Product> = runCatching {
        withContext(ioDispatcher) {
            suspendTransaction(db = database) {
                // Optional input guard:
                val entity = ProductEntity.findById(id.id)
                    ?: throw ProductNotFoundException(id)
                entity.toProduct()
            }
        }
    }
}

/**
 * Converts a [ProductEntity] instance into a [Product] domain model.
 *
 * @return A [Product] object with data copied from the current [ProductEntity].
 */
private fun ProductEntity.toProduct(): Product {
    return Product(
        id = ProductId(id.value),
        name = name,
        price = price,
        description = description,
        images = images,
        material = material,
        inStock = inStock,
        countryOfOrigin = countryOfOrigin
    )
}
