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
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * The key for the named dependency that provides the base URL for the backend.
 */
private const val BACKEND_BASE_URL = "backendBaseUrl"

/**
 * Platform-specific module for SQL database dependencies.
 * This module is expected to be implemented by each platform (Android, iOS)
 * and provide the necessary dependencies for SQL database operations,
 * such as the [SqlDriverFactory].
 */
expect val sqlPlatformModule: Module

/**
 * Koin module for common SQLDelight dependencies.
 * This module provides the necessary dependencies for interacting with the SQLDelight database,
 * including the database driver, the database instance, and any custom column adapters.
 * It includes platform-specific SQL driver configurations via `sqlPlatformModule`.
 */
val sqlCommonModule = module {
    includes(sqlPlatformModule)
    factoryOf(::DollarAmountAdapter)
    single { get<SqlDriverFactory>().createDriver() }
    single { ProductDatabase(driver = get(), product_basicAdapter = get()) }
}

/**
 * Koin module for application-level dependencies.
 * This module includes dependencies related to networking, data sources, repositories, use cases, and view models.
 */
val applicationModule = module {
    includes(sqlCommonModule)
    singleOf(::KtorClient)
    single(named(BACKEND_BASE_URL)) { "https://api.aoriani.dev/graphql" }
    single { ApolloClient(get(qualifier = named(BACKEND_BASE_URL)), ktorClient = get()) }
    singleOf(::GraphQlProductDataSource) bind ProductDataSource::class
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
    singleOf(::CartRepositoryImpl) bind CartRepository::class
    factoryOf(::AddToCartUseCaseImpl) bind AddToCartUseCase::class
    viewModel { ProductListViewModel(productRepository = get(), cartRepository = get(), addToCartUseCase = get()) }
    viewModel {
        ProductDetailsViewModel(
            productRepository = get(),
            addToCartUseCase = get(),
            savedStateHandle = get()
        )
    }
}