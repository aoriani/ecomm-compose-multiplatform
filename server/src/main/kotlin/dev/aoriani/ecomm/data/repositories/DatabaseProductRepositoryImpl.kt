package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.data.database.ProductEntity
import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

/**
 * Exposed-based implementation of the [ProductRepository].
 * Uses [ProductEntity] and [dev.aoriani.ecomm.data.database.ProductTable] to interact with the database.
 */
object DatabaseProductRepositoryImpl : ProductRepository {
    /**
     * Retrieves all products from the database.
     * Operations are performed within a new suspended transaction on [Dispatchers.IO].
     * @return A list of all [Product]s.
     */
    override suspend fun getAll(): Result<List<Product>> = newSuspendedTransaction(Dispatchers.IO) {
        Result.success(ProductEntity.all().map(ProductEntity::toProduct))
    }

    /**
     * Retrieves a product by its unique identifier from the database.
     * Operations are performed within a new suspended transaction on [Dispatchers.IO].
     * @param id The unique ID of the product.
     * @return The [Product] if found, otherwise null.
     */
    override suspend fun getById(id: String): Result<Product> = newSuspendedTransaction(Dispatchers.IO) {
        ProductEntity.findById(id)?.toProduct()?.let { Result.success(it) } ?: Result.failure(
            ProductNotFoundException("Product with id $id not found")
        )
    }
}

private fun ProductEntity.toProduct(): Product {
    return Product(
        id = id.value,
        name = name,
        price = price,
        description = description,
        images = images,
        material = material,
        inStock = inStock,
        countryOfOrigin = countryOfOrigin
    )
}