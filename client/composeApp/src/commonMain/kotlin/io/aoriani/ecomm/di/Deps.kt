package io.aoriani.ecomm.di

import io.aoriani.ecomm.data.network.ApolloClient
import io.aoriani.ecomm.data.network.KtorClient
import io.aoriani.ecomm.data.repositories.ProductRepository
import io.aoriani.ecomm.data.repositories.ProductRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
val appModule = module {
    singleOf(::KtorClient)
    single { ApolloClient("https://aoriani.dev/graphql", get()) }
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
}