package io.aoriani.ecomm.data.repositories

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.exception.ApolloException
import io.aoriani.ecomm.data.graphql.FetchProductQuery
import io.aoriani.ecomm.data.graphql.ListProductsQuery
import io.aoriani.ecomm.data.model.Product
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class ProductRepositoryImpl(private val apolloClient: ApolloClient) : ProductRepository {

    override suspend fun fetchProducts(): ImmutableList<Product> {
        val response: ApolloResponse<ListProductsQuery.Data>
        try {
            response = apolloClient.query(ListProductsQuery()).execute()
        } catch (apolloException: ApolloException) {
            throw ProductRepository.GraphQlException(
                apolloException.message.orEmpty(),
                apolloException
            )
        }
        if (response.hasErrors()) {
            throw ProductRepository.GraphQlException(response.errors?.map { it.message }.toString())
        } else {
            return response.data?.products?.map { product ->
                Product(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    description = product.description,
                    images = product.images.toImmutableList(),
                )
            }?.toImmutableList() ?: persistentListOf()
        }
    }

    override suspend fun getProduct(id: String): Product {
        val response: ApolloResponse<FetchProductQuery.Data>
        try {
            response = apolloClient.query(FetchProductQuery(id)).execute()
        } catch (apolloException: ApolloException) {
            throw ProductRepository.GraphQlException(
                apolloException.message.orEmpty(),
                apolloException
            )
        }

        val product = response.data?.product
        return if (response.hasErrors()) {
            throw ProductRepository.GraphQlException(response.errors?.map { it.message }.toString())
        } else if (product != null) {
            Product(
                id = product.id,
                name = product.name,
                price = product.price,
                description = product.description,
                images = product.images.toImmutableList(),
            )
        } else {
            throw ProductRepository.GraphQlException(
                "Product not found or missing data for id: $id"
            )
        }
    }
}