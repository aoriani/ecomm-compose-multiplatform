package io.aoriani.ecomm.di

import io.aoriani.ecomm.data.network.ApolloClient
import io.aoriani.ecomm.data.network.KtorClient
import io.aoriani.ecomm.data.repositories.ProductRepositoryImpl

object Deps {
    val ktorClient = KtorClient()
    val apolloClient = ApolloClient("https://aoriani.dev/graphql", ktorClient)
    val productRepository = ProductRepositoryImpl(apolloClient)
}