package io.aoriani.ecomm.data.repositories.products.datasources.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloException
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.data.repositories.products.datasources.ProductDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Implementation of [ProductDataSource] that fetches product data using GraphQL.
 * This class interacts with an [ApolloClient] to execute GraphQL queries for product listing and retrieval.
 * @param apolloClient The ApolloClient instance used for GraphQL operations.
 */
class GraphQlProductDataSource(private val apolloClient: ApolloClient) : ProductDataSource {
    /**
     * Fetches a list of all product previews from the GraphQL API.
     * Handles Apollo GraphQL responses, including errors, and maps the data to [ProductPreview] models.
     * @return A [Result] containing an [ImmutableList] of [ProductPreview] on success, or a [ProductRepository.ProductException] on failure.
     */
    override suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>> {
        return try {
            val response: ApolloResponse<ListProductsQuery.Data> =
                apolloClient.query(ListProductsQuery()).execute()

            when {
                response.exception != null -> {
                    handleNetworkError(response.exception)
                }

                response.hasErrors() -> {
                    handleGraphQlError(response)
                }

                else -> {
                    val products: ImmutableList<ProductPreview> =
                        response.data?.products?.map { product ->
                            product.toProductPreviewModel()
                        }?.toImmutableList() ?: persistentListOf()
                    Result.success(products)
                }
            }
        } catch (apolloException: ApolloException) {
            handleNetworkError(apolloException)
        }
    }

    private inline fun <reified T, E : ApolloException?> handleNetworkError(exception: E): Result<T> {
        return Result.failure(
            ProductRepository.ProductException(
                exception?.message.orEmpty(), exception
            )
        )
    }

    private inline fun <reified D : Operation.Data, T> handleGraphQlError(response: ApolloResponse<D>): Result<T> {
        val errorMessages =
            response.errors?.joinToString(separator = "\n") { it.message }
                ?: "Unknown GraphQL error"
        return Result.failure(ProductRepository.ProductException(errorMessages))
    }

    /**
     * Retrieves a single product by its unique identifier from the GraphQL API.
     * Handles Apollo GraphQL responses, including errors, and maps the data to a [Product] model.
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] containing the [Product] if found, or `null` when not found. On failures, returns a [ProductRepository.ProductException].
     */
    override suspend fun getProduct(id: String): Result<Product?> {
        return try {
            val response: ApolloResponse<FetchProductQuery.Data> =
                apolloClient.query(FetchProductQuery(id)).execute()

            when {
                response.exception != null -> {
                    handleNetworkError(response.exception)
                }

                response.hasErrors() -> {
                    handleGraphQlError(response)
                }

                else -> {
                    val product = response.data?.product?.toProductModel()
                    // Align with repository contract: not-found is success(null)
                    Result.success(product)
                }
            }
        } catch (apolloException: ApolloException) {
            handleNetworkError(apolloException)
        }
    }
}

/**
 * Converts a [ListProductsQuery.Product] GraphQL object to a [ProductPreview] domain model.
 * @return A [ProductPreview] object containing the mapped data.
 */
private fun ListProductsQuery.Product.toProductPreviewModel(): ProductPreview = ProductPreview(
    id = ProductBasic.Id(productBasic.id),
    name = productBasic.name,
    price = DollarAmount(productBasic.price.toString()),
    thumbnailUrl = productBasic.images.firstOrNull(),
)

/**
 * Converts a [FetchProductQuery.Product] GraphQL object to a [Product] domain model.
 * @return A [Product] object containing the mapped data.
 */
private fun FetchProductQuery.Product.toProductModel(): Product = Product(
    id = ProductBasic.Id(productBasic.id),
    name = productBasic.name,
    price = DollarAmount(productBasic.price.toString()),
    description = description,
    images = productBasic.images.toImmutableList(),
    material = material,
    countryOfOrigin = countryOfOrigin,
    inStock = inStock
)
