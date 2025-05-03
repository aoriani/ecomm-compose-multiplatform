package dev.aoriani.ecomm.graphql.queries

import com.expediagroup.graphql.server.operations.Query
import dev.aoriani.ecomm.graphql.repository.ProductRepository
import dev.aoriani.ecomm.graphql.models.Product

class ProductQuery: Query {
    fun products(): List<Product> = ProductRepository.getAll()
    fun product(id: String): Product? = ProductRepository.getById(id)
}