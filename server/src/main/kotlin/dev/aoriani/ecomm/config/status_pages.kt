package dev.aoriani.ecomm.config

import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

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
        exception<Throwable> { call, cause ->
            call.application.log.error("An unhandled error occurred", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}