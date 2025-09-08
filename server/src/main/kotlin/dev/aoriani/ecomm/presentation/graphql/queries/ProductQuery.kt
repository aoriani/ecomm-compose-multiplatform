package dev.aoriani.ecomm.presentation.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.BlankProductIdException
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.domain.usecases.invoke
import dev.aoriani.ecomm.presentation.graphql.exceptions.GraphQLInternalException
import dev.aoriani.ecomm.presentation.graphql.models.Product
import dev.aoriani.ecomm.presentation.graphql.models.toGraphQlProduct
import org.slf4j.LoggerFactory

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

    private val logger = LoggerFactory.getLogger(javaClass)

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
            val exception = result.exceptionOrNull()
            logger.error("Failed to get all products", exception)
            throw GraphQLInternalException("Failed to retrieve products", exception)
        } else {
            return result.getOrNull()?.map { it.toGraphQlProduct() } ?: emptyList()
        }
    }

    /**
     * Fetches a single product by its unique identifier.
     *
     * Looks up a product using the provided ID. If no product exists for the
     * given ID, this method returns null. If the provided ID is blank, a
     * [BlankProductIdException] is thrown. Any other unexpected errors are
     * wrapped and rethrown as [GraphQLInternalException].
     *
     * @param id The unique ID of the product to retrieve. Must not be blank.
     * @return The product for the provided ID, or null if not found.
     * @throws BlankProductIdException If the provided ID is blank.
     */
    @GraphQLDescription("Fetch a single product by ID. Returns null when not found.")
    suspend fun product(
        @GraphQLDescription("Product ID to retrieve. Must be non-empty.")
        id: ID
    ): Product? {
        val result = getProductById(ProductId(id.value))
        return if (result.isFailure) {
            when (val exception = result.exceptionOrNull()) {
                is ProductNotFoundException -> null
                is BlankProductIdException -> throw exception
                else -> {
                    logger.error("Failed to get product by id: ${id.value}", exception)
                    throw GraphQLInternalException("Failed to retrieve product with id: ${id.value}", exception)
                }
            }
        } else {
            result.getOrThrow().toGraphQlProduct()
        }
    }
}
