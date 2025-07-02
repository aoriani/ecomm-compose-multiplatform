package dev.aoriani.ecomm.repository.database

import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseProductRepositoryImpl : ProductRepository {
    override fun getAll(): List<Product> = transaction {  ProductEntity.all().map(ProductEntity::toProduct) }


    override fun getById(id: String): Product? = transaction {  ProductEntity.findById(id)?.toProduct() }
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