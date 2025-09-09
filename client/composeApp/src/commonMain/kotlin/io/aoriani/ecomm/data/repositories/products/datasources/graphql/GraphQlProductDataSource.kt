package io.aoriani.ecomm.data.repositories.products.datasources.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
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
     * Processes the Apollo GraphQL response, handling errors and mapping data.
     * This function checks for exceptions and GraphQL errors in the response.
     * If errors are present, it returns a [Result.failure] with a [ProductRepository.ProductException].
     * Otherwise, it invokes the provided [dataMapper] with the response data to produce a [Result.success].
     * @param R The type of the data to be returned in the [Result].
     * @param D The type of the GraphQL operation data.
     * @param response The [ApolloResponse] to process.
     * @param dataMapper A lambda function that takes the GraphQL data of type [D] (or null) and returns a [Result] of type [R].
     * @return A [Result] of type [R], which can be a success with the mapped data or a failure with an exception.
     */
    private inline fun <reified R, D : Operation.Data> processResponse(
        response: ApolloResponse<D>,
        dataMapper: (D?) -> Result<R>
    ): Result<R> {
        return when {
            response.exception != null -> {
                Result.failure(
                    ProductRepository.ProductException(
                        response.exception?.message.orEmpty(), response.exception
                    )
                )
            }

            response.hasErrors() -> {
                val errorMessages =
                    response.errors?.joinToString(separator = "\n") { it.message }
                        ?: "Unknown GraphQL error"
                Result.failure(ProductRepository.ProductException(errorMessages))
            }

            else -> {
                val data = response.data
                dataMapper(data)
            }
        }
    }

    /**
     * Fetches a list of all product previews from the GraphQL API.
     * Handles Apollo GraphQL responses, including errors, and maps the data to [ProductPreview] models.
     * @return A [Result] containing an [ImmutableList] of [ProductPreview] on success, or a [ProductRepository.ProductException] on failure.
     */
    override suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>> {
        val response: ApolloResponse<ListProductsQuery.Data> =
            apolloClient.query(ListProductsQuery()).execute()

        return processResponse(response) { data ->
            val products: ImmutableList<ProductPreview> =
                data?.products?.map { product ->
                    product.toProductPreviewModel()
                }?.toImmutableList() ?: persistentListOf()
            Result.success(products)
        }

    }

    /**
     * Retrieves a single product by its unique identifier from the GraphQL API.
     * Handles Apollo GraphQL responses, including errors, and maps the data to a [Product] model.
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] containing the [Product] if found, or `null` when not found. On failures, returns a [ProductRepository.ProductException].
     */
    override suspend fun getProduct(id: String): Result<Product?> {
        val response: ApolloResponse<FetchProductQuery.Data> =
            apolloClient.query(FetchProductQuery(id)).execute()

        return processResponse(response) { data ->
            val product: Product? = data?.product?.toProductModel()
            Result.success(product)
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
