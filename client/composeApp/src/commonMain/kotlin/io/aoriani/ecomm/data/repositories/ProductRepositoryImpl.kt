package io.aoriani.ecomm.data.repositories

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import co.touchlab.kermit.Logger
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.exception.ApolloException
import io.aoriani.ecomm.data.graphql.FetchProductQuery
import io.aoriani.ecomm.data.graphql.ListProductsQuery
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.db.ProductDatabase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductRepositoryImpl(private val apolloClient: ApolloClient, private val productDatabase: ProductDatabase) : ProductRepository, KoinComponent {

    init {
       GlobalScope.launch {
           try {
               Logger.i("DATABASE") { "DB started" }
               val driver: SqlDriver by inject()
               ProductDatabase.Schema.awaitCreate(driver)
               productDatabase.productDatabaseQueries.transaction(false) {
                   productDatabase.productDatabaseQueries.insertProductImage("halli", "hello")
                   productDatabase.productDatabaseQueries.insertProductBasic("halli", "world", 23.0)
               }
               productDatabase.productDatabaseQueries.getProductPreviews().awaitAsList().forEach { product ->println(product) }
               Logger.i("DATABASE") { "DB Succeed" }
           } catch (e: Exception) {
               Logger.i("DATABASE", e) { "DB Failed" }
           }
       }
    }

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
            val errorMessages = response.errors?.joinToString(separator = "\n") { it.message }
                ?: "Unknown GraphQL error"
            throw ProductRepository.GraphQlException(errorMessages)
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
            val errorMessages = response.errors?.joinToString(separator = "\n") { it.message }
                ?: "Unknown GraphQL error"
            throw ProductRepository.GraphQlException(errorMessages)
        } else {
            product?.toProductModel() ?: throw ProductRepository.GraphQlException(
                "Product not found or missing data for id: $id"
            )
        }
    }
}

private fun ListProductsQuery.Product.toProductPreviewModel(): ProductPreview = ProductPreview(
    id = productBasic.id,
    name = productBasic.name,
    price = DollarAmount(productBasic.price.toString()),
    thumbnailUrl = productBasic.images.firstOrNull(),
)

private fun FetchProductQuery.Product.toProductModel(): Product = Product(
    id = productBasic.id,
    name = productBasic.name,
    price = DollarAmount(productBasic.price.toString()),
    description = description,
    images = productBasic.images.toImmutableList(),
    material = material,
    countryOfOrigin = countryOfOrigin,
    inStock = inStock
)
