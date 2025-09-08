package io.aoriani.ecomm.data.repositories.products.datasources.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
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

            if (response.hasErrors()) {
                val errorMessages = response.errors?.joinToString(separator = "\n") { it.message }
                    ?: "Unknown GraphQL error"
                Result.failure(ProductRepository.ProductException(errorMessages))
            } else {
                val products: ImmutableList<ProductPreview> =
                    response.data?.products?.map { product ->
                        product.toProductPreviewModel()
                    }?.toImmutableList() ?: persistentListOf()
                Result.success(products)
            }
        } catch (apolloException: ApolloException) {
            Result.failure(
                ProductRepository.ProductException(
                    apolloException.message.orEmpty(), apolloException
                )
            )
        }
    }

    /**
     * Retrieves a single product by its unique identifier from the GraphQL API.
     * Handles Apollo GraphQL responses, including errors, and maps the data to a [Product] model.
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] containing the [Product] if found, or a [ProductRepository.ProductException] if not found or an error occurs.
     */
    override suspend fun getProduct(id: String): Result<Product?> {
        return try {
            val response: ApolloResponse<FetchProductQuery.Data> =
                apolloClient.query(FetchProductQuery(id)).execute()

            if (response.hasErrors()) {
                val errorMessages = response.errors?.joinToString(separator = "\n") { it.message }
                    ?: "Unknown GraphQL error"
                Result.failure(ProductRepository.ProductException(errorMessages))
            } else {
                val product = response.data?.product?.toProductModel()
                if (product != null) {
                    Result.success(product)
                } else {
                    Result.failure(
                        ProductRepository.ProductException(
                            "Product not found or missing data for id: $id"
                        )
                    )
                }
            }
        } catch (apolloException: ApolloException) {
            Result.failure(
                ProductRepository.ProductException(
                    apolloException.message.orEmpty(), apolloException
                )
            )
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
