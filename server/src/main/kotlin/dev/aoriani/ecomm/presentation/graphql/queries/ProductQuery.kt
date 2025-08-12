package dev.aoriani.ecomm.presentation.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import dev.aoriani.ecomm.domain.models.ProductNotFoundException
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.domain.usecases.invoke
import dev.aoriani.ecomm.presentation.graphql.models.Product
import dev.aoriani.ecomm.presentation.graphql.models.toGraphQlProduct

/**
 * GraphQL query resolver for products.
 *
 * Delegates to domain use cases `GetAllProductsUseCase` and `GetProductByIdUseCase`
 * for data access, then maps results to GraphQL models via `toGraphQlProduct`.
 *
 * `@GraphQLDescription` annotations are surfaced in the generated schema docs.
 */
@GraphQLDescription("Root entry point for product-related queries")
class ProductQuery(
    private val getAllProducts: GetAllProductsUseCase,
    private val getProductById: GetProductByIdUseCase
) : Query {

    /**
     * Retrieves all products available in the catalog.
     *
     * This method interacts with the underlying domain use case to fetch all products,
     * and maps them to GraphQL-compatible models.
     *
     * @return A list of products if available, or an empty list if no products are found.
     * @throws RuntimeException if an unknown error occurs during retrieval.
     */
    @GraphQLDescription("Retrieve all products available in the catalog")
    suspend fun products(): List<Product> {
        val result = getAllProducts()
        if (result.isFailure) {
            //TODO: Log failure
            throw RuntimeException("Unknown error")
        } else {
            return result.getOrNull()?.map { it.toGraphQlProduct() } ?: emptyList()
        }
    }

    /**
     * Fetches a single product by its unique identifier.
     *
     * This method retrieves a product using the given ID. If the product does not exist,
     * a `ProductNotFoundException` is thrown. Additionally, if the ID is blank, an
     * `IllegalArgumentException` is thrown.
     *
     * @param id The unique ID of the product to retrieve. Must not be blank.
     * @return The product corresponding to the provided ID.
     * @throws ProductNotFoundException If no product is found for the given ID.
     * @throws IllegalArgumentException If the provided ID is blank.
     */
    @GraphQLDescription("Fetch a single product by its unique identifier. Throws ProductNotFoundException if not found.")
    suspend fun product(
        @GraphQLDescription("The unique ID of the product to retrieve. Cannot be blank.")
        id: ID
    ): Product {
        require(id.value.isNotBlank()) { "Product ID cannot be blank." }
        val result = getProductById(id.value)
        if (result.isFailure) {
            // TODO: Log failure
            when (val exception = result.exceptionOrNull()) {
                is ProductNotFoundException -> throw exception
                else -> throw RuntimeException("Unknown error")
            }
        } else {
            return result.getOrThrow().toGraphQlProduct()
        }
    }
}
