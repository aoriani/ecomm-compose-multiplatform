package dev.aoriani.ecomm

import dev.aoriani.ecomm.config.configureCaching
import dev.aoriani.ecomm.config.configureCallLogging
import dev.aoriani.ecomm.config.configureCompression
import dev.aoriani.ecomm.config.configureCors
import dev.aoriani.ecomm.config.configureDatabase
import dev.aoriani.ecomm.config.configureGraphQL
import dev.aoriani.ecomm.config.configureMcp
import dev.aoriani.ecomm.config.configureRouting
import dev.aoriani.ecomm.config.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

/**
 * Entry point of the application.
 *
 * @param args Command-line arguments passed to the application.
 */
fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * Configures the main application module by setting up dependencies and various middleware components.
 *
 * The following configurations and features are initialized:
 *
 * - Dependency injection for the `ProductRepository` interface with a database-backed implementation.
 * - Call logging setup to monitor HTTP request and response details.
 * - HTTP response compression for optimizing data transfer size.
 * - Database connection and initial configuration, including data seeding if required.
 * - CORS settings for allowing specific hosts, methods, and headers in cross-origin resource sharing.
 * - GraphQL configuration with schema definition, query resolvers, and custom hooks.
 * - Routing setup for HTTP endpoints, including static content, GraphQL routes, and a default root route.
 * - Status page configuration for handling application errors and GraphQL-specific responses.
 */
fun Application.module() {
    configureDatabase()
    configureCallLogging()
    configureCaching()
    configureCompression()
    configureCors()
    configureGraphQL()
    configureMcp()
    configureRouting()
    configureStatusPages()
}
