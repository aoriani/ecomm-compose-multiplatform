package dev.aoriani.ecomm.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import dev.aoriani.ecomm.graphql.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository

/**
 * GraphQL Query resolver for product-related operations.
 * Utilizes a [ProductRepository] for data access.
 *
 * The `@GraphQLDescription` annotations on the class and its methods
 * are used by `graphql-kotlin` to generate the GraphQL schema documentation.
 */
@GraphQLDescription("Root entry point for product-related queries")
class ProductQuery(private val repository: ProductRepository) : Query {

    @GraphQLDescription("Retrieve all products available in the catalog")
    suspend fun products(): List<Product> = repository.getAll()

    @GraphQLDescription("Fetch a single product by its unique identifier. Throws ProductNotFoundException if not found.")
    suspend fun product(
        @GraphQLDescription("The unique ID of the product to retrieve. Cannot be blank.")
        id: String
    ): Product {
        if (id.isBlank()) {
            throw IllegalArgumentException("Product ID cannot be blank.")
        }
        return repository.getById(id) ?: throw ProductNotFoundException(id)
    }
}