package dev.aoriani.ecomm.repository.database

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

/**
 * Exposed-based implementation of the [ProductRepository].
 * Uses [ProductEntity] and [ProductTable] to interact with the database.
 */
internal object DatabaseProductRepositoryImpl : ProductRepository {
    /**
     * Retrieves all products from the database.
     * Operations are performed within a new suspended transaction on [Dispatchers.IO].
     * @return A list of all [Product]s.
     */
    override suspend fun getAll(): List<Product> = newSuspendedTransaction(Dispatchers.IO) {
        ProductEntity.all().map(ProductEntity::toProduct)
    }

    /**
     * Retrieves a product by its unique identifier from the database.
     * Operations are performed within a new suspended transaction on [Dispatchers.IO].
     * @param id The unique ID of the product.
     * @return The [Product] if found, otherwise null.
     */
    override suspend fun getById(id: String): Product? = newSuspendedTransaction(Dispatchers.IO) {
        ProductEntity.findById(id)?.toProduct()
    }
}

private fun ProductEntity.toProduct(): Product {
    return Product(
        id = ID(id.value),
        name = name,
        price = price,
        description = description,
        images = images,
        material = material,
        inStock = inStock,
        countryOfOrigin = countryOfOrigin
    )
}