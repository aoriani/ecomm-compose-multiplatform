package io.aoriani.io.aoriani.ecomm.graphql.queries

import com.expediagroup.graphql.server.operations.Query
import io.aoriani.ecomm.graphql.models.Product
import io.aoriani.io.aoriani.ecomm.graphql.repository.ProductRepository

class ProductQuery: Query {
    fun products(): List<Product> = ProductRepository.getAll()
    fun product(id: String): Product? = ProductRepository.getById(id)
}