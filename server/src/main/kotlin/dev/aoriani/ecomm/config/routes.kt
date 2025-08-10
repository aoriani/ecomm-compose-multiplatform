package dev.aoriani.ecomm.config

import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Represents the root route path for the application's routing configuration.
 * Used to define the base endpoint for root-level requests.
 */
private const val ROOT_ROUTE = "/"

/**
 * Specifies the base route path for serving static resources within the application.
 */
private const val STATIC_ROUTE = "/static"

/**
 * The directory name where static resources are located.
 * Used in configuring the routing for serving static files in the application.
 */
private const val STATIC_RESOURCE_DIR = "static"

/**
 * Sets up the routing configuration for the Ktor application.
 *
 * This method defines the application's main HTTP routes, including:
 * - A root route (`ROOT_ROUTE`, defined in the application's configuration)
 *   that responds with a "Hello World!" message.
 * - Static resources served from a directory (`STATIC_RESOURCE_DIR`) via the route
 *   defined by `STATIC_ROUTE`.
 * - GraphQL-specific routes for handling GraphQL HTTP POST, GET requests, and the
 *   GraphiQL web interface.
 *
 * Usage of this method integrates routing features, enabling the application to handle
 * static content, GraphQL operations, and the default entry point for HTTP requests.
 */
internal fun Application.configureRouting() {
    routing {
        get(ROOT_ROUTE) {
            call.respondText("Hello World!")
        }
        staticResources(STATIC_ROUTE, STATIC_RESOURCE_DIR)
        graphQLPostRoute()
        graphQLGetRoute()
        graphiQLRoute()
    }
}