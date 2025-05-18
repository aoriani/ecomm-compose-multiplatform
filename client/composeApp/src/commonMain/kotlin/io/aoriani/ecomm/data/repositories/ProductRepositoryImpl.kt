package io.aoriani.ecomm.data.repositories

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.exception.ApolloException
import io.aoriani.ecomm.data.graphql.FetchProductQuery
import io.aoriani.ecomm.data.graphql.ListProductsQuery
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class ProductRepositoryImpl(private val apolloClient: ApolloClient) : ProductRepository {

    override suspend fun fetchProducts(): ImmutableList<ProductPreview> {
        val response: ApolloResponse<ListProductsQuery.Data>
        try {
            response = apolloClient.query(ListProductsQuery()).execute()
        } catch (apolloException: ApolloException) {
            throw ProductRepository.GraphQlException(
                apolloException.message.orEmpty(), apolloException
            )
        }
        if (response.hasErrors()) {
            throw ProductRepository.GraphQlException(response.errors?.map { it.message }.toString())
        } else {
            return response.data?.products?.map { product ->
                product.toProductPreviewModel()
            }?.toImmutableList() ?: persistentListOf()
        }
    }

    override suspend fun getProduct(id: String): Product {
        val response: ApolloResponse<FetchProductQuery.Data>
        try {
            response = apolloClient.query(FetchProductQuery(id)).execute()
        } catch (apolloException: ApolloException) {
            throw ProductRepository.GraphQlException(
                apolloException.message.orEmpty(), apolloException
            )
        }

        val product = response.data?.product
        return if (response.hasErrors()) {
            throw ProductRepository.GraphQlException(response.errors?.map { it.message }.toString())
        } else {
            product?.toProductModel() ?: throw ProductRepository.GraphQlException(
                "Product not found or missing data for id: $id"
            )
        }
    }
}

private fun ListProductsQuery.Product.toProductPreviewModel(): ProductPreview = ProductPreview(
    id = id,
    name = name,
    price = price,
    thumbnail = images.firstOrNull(),
)

private fun FetchProductQuery.Product.toProductModel(): Product = Product(
    id = id,
    name = name,
    price = price,
    description = description,
    images = images.toImmutableList(),
)