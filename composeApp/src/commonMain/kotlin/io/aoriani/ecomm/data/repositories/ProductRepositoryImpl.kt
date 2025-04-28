package io.aoriani.ecomm.data.repositories

import com.apollographql.apollo.ApolloClient
import io.aoriani.ecomm.data.graphql.platzi.FetchProductQuery
import io.aoriani.ecomm.data.graphql.platzi.ListProductsQuery
import io.aoriani.ecomm.data.model.Product
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class ProductRepositoryImpl {
    class ProductRepositoryImpl(private val apolloClient: ApolloClient) : ProductRepository {

        override suspend fun listProducts(): ImmutableList<Product> {
            val response = apolloClient.query(ListProductsQuery()).execute()
            return response.data?.products?.map { product ->
                Product(
                    id = product.id,
                    title = product.title,
                    price = product.price,
                    description = product.description,
                    images = product.images.toImmutableList(),
                    categoryName = product.category.name
                )
            }?.toImmutableList() ?: persistentListOf()
        }

        override suspend fun getProduct(id: String): Product {
            val response = apolloClient.query(FetchProductQuery(id)).execute()
            val product = response.data?.product ?: throw Exception("Product not found")
            return Product(
                id = product.id,
                title = product.title,
                price = product.price,
                description = product.description,
                images = product.images.toImmutableList(),
                categoryName = product.category.name
            )
        }
    }
}