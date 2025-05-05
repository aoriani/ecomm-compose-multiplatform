package io.aoriani.ecomm.di

import io.aoriani.ecomm.data.network.ApolloClient
import io.aoriani.ecomm.data.repositories.ProductRepositoryImpl

object Deps {
    val apolloClient = ApolloClient("https://aoriani.dev/graphql")
    val productRepository = ProductRepositoryImpl(apolloClient)
}