package io.aoriani.ecomm.di

import io.aoriani.ecomm.data.network.ApolloClient
import io.aoriani.ecomm.data.network.KtorClient
import io.aoriani.ecomm.data.repositories.cart.CartRepository
import io.aoriani.ecomm.data.repositories.cart.CartRepositoryImpl
import io.aoriani.ecomm.data.repositories.db.DollarAmountAdapter
import io.aoriani.ecomm.data.repositories.db.ProductDatabase
import io.aoriani.ecomm.data.repositories.db.SqlDriverFactory
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.data.repositories.products.ProductRepositoryImpl
import io.aoriani.ecomm.data.repositories.products.datasources.ProductDataSource
import io.aoriani.ecomm.data.repositories.products.datasources.graphql.GraphQlProductDataSource
import io.aoriani.ecomm.domain.AddToCartUseCase
import io.aoriani.ecomm.domain.AddToCartUseCaseImpl
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsViewModel
import io.aoriani.ecomm.ui.screens.productlist.ProductListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val BACKEND_BASE_URL = "backendBaseUrl"

expect val sqlPlatformModule: Module

val sqlCommonModule = module {
    includes(sqlPlatformModule)
    factoryOf(::DollarAmountAdapter)
    single { get<SqlDriverFactory>().createDriver() }
    single { ProductDatabase(driver = get(), product_basicAdapter = get()) }
}

val appModule = module {
    includes(sqlCommonModule)
    singleOf(::KtorClient)
    single(named(BACKEND_BASE_URL)) { "https://api.aoriani.dev/graphql" }
    single { ApolloClient(get(qualifier = named(BACKEND_BASE_URL)), ktorClient = get()) }
    singleOf(::GraphQlProductDataSource) bind ProductDataSource::class
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
    singleOf(::CartRepositoryImpl) bind CartRepository::class
    factoryOf(::AddToCartUseCaseImpl) bind AddToCartUseCase::class
    viewModel { ProductListViewModel(productRepository = get(), addToCartUseCase = get()) }
    viewModel {
        ProductDetailsViewModel(
            productRepository = get(),
            addToCartUseCase = get(),
            savedStateHandle = get()
        )
    }
}