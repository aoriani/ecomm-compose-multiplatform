package dev.aoriani.ecomm.config

import com.expediagroup.graphql.server.ktor.GraphQL
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.graphql.hooks.ProductSchemaGeneratorHooks
import dev.aoriani.ecomm.presentation.graphql.queries.ProductQuery
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.di.dependencies

/**
 * Configures the GraphQL server for the application.
 *
 * This method sets up the `GraphQL` feature with the following:
 * - Schema generation using specified package paths.
 * - Query resolvers to handle product-related queries.
 * - Custom hooks for additional schema customization or behavior adjustments.
 *
 * The schema is dynamically built by scanning classes within the provided packages for GraphQL types
 * and directives, and associating them with the specified query resolvers.
 *
 * Dependencies required for query resolver initialization are retrieved using the application's dependency injection mechanism.
 */
internal fun Application.configureGraphQL() {
    install(GraphQL) {
        schema {
            packages = listOf("dev.aoriani.ecomm.presentation.graphql.models", "java.math")
            val getAllProductsUseCase: GetAllProductsUseCase by dependencies
            val getProductByIdUseCase: GetProductByIdUseCase by dependencies
            queries = listOf(
                ProductQuery(
                    getAllProducts = getAllProductsUseCase,
                    getProductById = getProductByIdUseCase
                )
            )
            hooks = ProductSchemaGeneratorHooks
        }
    }
}