package dev.aoriani.ecomm.presentation.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import dev.aoriani.ecomm.domain.models.ProductNotFoundException
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.graphql.models.Product
import dev.aoriani.ecomm.presentation.graphql.models.toGraphQlProduct
import dev.aoriani.ecomm.domain.usecases.invoke

/**
 * GraphQL Query resolver for product-related operations.
 * Utilizes a [dev.aoriani.ecomm.repository.ProductRepository] for data access.
 *
 * The `@GraphQLDescription` annotations on the class and its methods
 * are used by `graphql-kotlin` to generate the GraphQL schema documentation.
 */
@GraphQLDescription("Root entry point for product-related queries")
class ProductQuery(
    private val getAllProducts: GetAllProductsUseCase,
    private val getProductById: GetProductByIdUseCase
) : Query {

    @GraphQLDescription("Retrieve all products available in the catalog")
    suspend fun products(): List<Product> = getAllProducts().getOrThrow().map { it.toGraphQlProduct() }

    @GraphQLDescription("Fetch a single product by its unique identifier. Throws ProductNotFoundException if not found.")
    suspend fun product(
        @GraphQLDescription("The unique ID of the product to retrieve. Cannot be blank.")
        id: ID
    ): Product {
        require(id.value.isNotBlank()) { "Product ID cannot be blank." }
        return getProductById(id.value).getOrNull()?.toGraphQlProduct() ?: throw ProductNotFoundException(id.value)
    }
}