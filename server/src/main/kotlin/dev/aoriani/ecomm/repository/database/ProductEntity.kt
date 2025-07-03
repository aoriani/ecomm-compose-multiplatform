package dev.aoriani.ecomm.repository.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.ImmutableEntityClass

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