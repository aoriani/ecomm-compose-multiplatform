package dev.aoriani.ecomm.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository

@GraphQLDescription("Root entry point for product-related queries")
class ProductQuery(private val repository: ProductRepository) : Query {

    @GraphQLDescription("Retrieve all products available in the catalog")
    fun products(): List<Product> = repository.getAll()

    @GraphQLDescription("Fetch a single product by its unique identifier")
    fun product(
        @GraphQLDescription("The unique ID of the product to retrieve")
        id: String
    ): Product? = repository.getById(id)
}