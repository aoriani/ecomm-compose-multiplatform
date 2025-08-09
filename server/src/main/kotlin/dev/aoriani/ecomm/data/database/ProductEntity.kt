package dev.aoriani.ecomm.data.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.ImmutableEntityClass

/**
 * DAO entity representing a row in [ProductTable] (package dev.aoriani.ecomm.data.database).
 *
 * Backed by Exposed v1 DAO APIs, this class provides a read-only, object-oriented view of a product record.
 *
 * @property id The primary key of the product entity.
 * @property name The product name.
 * @property price The product price as a BigDecimal.
 * @property description A detailed description of the product.
 * @property material The material composition of the product.
 * @property images A list of image URLs (stored as JSON in the table).
 * @property inStock Whether the product is currently in stock.
 * @property countryOfOrigin The country where the product originates.
 */
class ProductEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : ImmutableEntityClass<String, ProductEntity>(ProductTable)

    val name by ProductTable.name
    val price by ProductTable.price
    val description by ProductTable.description
    val material by ProductTable.material
    val images by ProductTable.images
    val inStock by ProductTable.inStock
    val countryOfOrigin by ProductTable.countryOfOrigin
}