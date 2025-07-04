package io.aoriani.ecomm.di

import io.aoriani.ecomm.data.network.ApolloClient
import io.aoriani.ecomm.data.network.KtorClient
import io.aoriani.ecomm.data.repositories.ProductRepository
import io.aoriani.ecomm.data.repositories.ProductRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val BACKEND_BASE_URL = "backendBaseUrl"

val appModule = module {
    singleOf(::KtorClient)
    single(named(BACKEND_BASE_URL)) { "https://aoriani.dev/graphql" }
    single { ApolloClient(get(named(BACKEND_BASE_URL)), get()) }
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
}