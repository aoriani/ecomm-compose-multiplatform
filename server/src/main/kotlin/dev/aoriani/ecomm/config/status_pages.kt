package dev.aoriani.ecomm.config

import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages

/**
 * Configures the status pages to handle application-level errors and exceptions.
 *
 * Registers a default set of GraphQL-specific HTTP status pages for processing
 * and responding to errors encountered during the application's execution.
 *
 * This setup leverages the `defaultGraphQLStatusPages` function, which provides
 * error responses tailored to GraphQL requests, ensuring consistent error handling
 * and meaningful feedback to the client side.
 */
internal fun Application.configureStatusPages() {
    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}