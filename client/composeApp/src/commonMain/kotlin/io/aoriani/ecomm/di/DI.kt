package io.aoriani.ecomm.di

import io.aoriani.ecomm.data.network.ApolloClient
import io.aoriani.ecomm.data.network.KtorClient
import io.aoriani.ecomm.data.repositories.ProductRepository
import io.aoriani.ecomm.data.repositories.ProductRepositoryImpl
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsViewModel
import io.aoriani.ecomm.ui.screens.productlist.ProductListViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    singleOf(::KtorClient)
    single { ApolloClient("https://aoriani.dev/graphql", get()) }
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class

    viewModelOf(::ProductListViewModel)
    viewModelOf(::ProductDetailsViewModel)
}
