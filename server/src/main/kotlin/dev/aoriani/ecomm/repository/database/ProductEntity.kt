package dev.aoriani.ecomm.repository.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.ImmutableEntityClass

/**
 * Represents a product entity stored in the database, mapped to the [ProductTable].
 *
 * This class provides an object-oriented view of a product record, allowing easy access
 * to its properties and interaction with the Exposed ORM framework.
 *
 * @property id The unique identifier of the product.
 * @property name The name of the product.
 * @property price The price of the product.
 * @property description The detailed description of the product.
 * @property material The material composition of the product.
 * @property images A list of image URLs for the product.
 * @property inStock A boolean indicating if the product is currently in stock.
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