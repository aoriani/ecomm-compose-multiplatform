package dev.aoriani.ecomm

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import dev.aoriani.ecomm.graphql.schemageneratorhooks.ProductSchemaGeneratorHooks
import dev.aoriani.ecomm.graphql.queries.ProductQuery
import dev.aoriani.ecomm.repository.ProductRepository
import dev.aoriani.ecomm.repository.database.DatabaseProductRepositoryImpl
import dev.aoriani.ecomm.repository.database.initializeDatabaseAndSeedIfEmpty
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.matchContentType
import io.ktor.server.plugins.compression.minimumSize
import io.ktor.server.plugins.conditionalheaders.ConditionalHeaders
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.contentType
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.slf4j.event.Level
import java.sql.Connection

/**
 * A list of HTTP methods that are permitted for Cross-Origin Resource Sharing (CORS) requests.
 *
 * This constant is used to define the allowed HTTP methods for CORS configurations within the application,
 * ensuring that only the specified methods are permitted for cross-origin requests.
 *
 * The list includes common HTTP methods:
 * - `HttpMethod.Get`
 * - `HttpMethod.Post`
 * - `HttpMethod.Put`
 * - `HttpMethod.Delete`
 * - `HttpMethod.Patch`
 * - `HttpMethod.Options`
 */
// Extracted constants for reuse
private val ALLOWED_CORS_METHODS = listOf(
    HttpMethod.Get, HttpMethod.Post, HttpMethod.Put, HttpMethod.Delete,
    HttpMethod.Patch, HttpMethod.Options
)
/**
 * A list of HTTP headers that are allowed for CORS (Cross-Origin Resource Sharing) requests.
 * These headers enable proper communication between different origins by explicitly permitting
 * specific request headers from clients.
 */
private val ALLOWED_CORS_HEADERS = listOf(
    HttpHeaders.ContentType, HttpHeaders.Authorization, "X-Requested-With"
)
/**
 * A map defining the hosts and their associated schemes that are allowed for Cross-Origin Resource Sharing (CORS).
 *
 * Each entry in the map consists of a host as the key (e.g., "localhost:8080") and a list of
 * schemes (e.g., ["http"]) defining the protocols that are allowed for that host.
 *
 * This configuration is used to specify permitted external resources for CORS, ensuring
 * that only specified hosts with certain schemes can interact with the application.
 */
private val ALLOWED_CORS_HOSTS = mapOf(
    "localhost:8080" to listOf("http"),
    "aoriani.dev" to listOf("https")
)

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
    configureCompressionAndCaching()
    configureCors()
    configureGraphQL()
    configureRouting()
    configureStatusPages()
}

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
private fun Application.configureGraphQL() {
    install(GraphQL) {
        schema {
            packages = listOf("dev.aoriani.ecomm.graphql.models", "java.math")
            val repository: ProductRepository by dependencies
            queries = listOf(ProductQuery(repository))
            hooks = ProductSchemaGeneratorHooks
        }
    }
}

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the application.
 *
 * This method sets up the necessary CORS policies to determine how cross-origin
 * requests are handled by the server. The configuration includes allowing specific
 * hosts, HTTP methods, and headers as defined in the application settings.
 *
 * The following configurations are applied:
 * - Allowed hosts: Iterates through a predefined list of hosts and their associated
 *   schemes to grant access.
 * - Allowed HTTP methods: Enables support for specific request methods determined by the settings.
 * - Allowed headers: Specifies a list of headers that can be sent in requests.
 * - Disabling credential sharing: Explicitly disallows the inclusion of credentials such as
 *   cookies or HTTP authentication in cross-origin requests.
 *
 * This setup ensures secure and controlled access to server resources
 * for clients outside the origin boundary.
 */
private fun Application.configureCors() {
    install(CORS) {
        ALLOWED_CORS_HOSTS.forEach { (host, schemes) -> allowHost(host, schemes = schemes) }
        ALLOWED_CORS_METHODS.forEach(::allowMethod)
        ALLOWED_CORS_HEADERS.forEach(::allowHeader)
        allowCredentials = false
    }
}

/**
 * Configures call logging for the application.
 *
 * This method sets up the `CallLogging` plugin to log details of incoming HTTP requests and outgoing responses.
 * The logging behavior is configurable based on the 'ecomm.logging.level' setting. If no specific level is
 * configured, the default logging level is set to `INFO`.
 *
 * The configuration includes:
 * - Setting the logging level dynamically from the application's environment configuration.
 * - Filtering to ensure only requests with paths starting with '/' are logged.
 * - Formatting the log messages to include additional request and response details, such as:
 *   - HTTP method
 *   - Request URI
 *   - Response status
 *   - Content type
 *   - Content length
 *   - User agent
 *
 * This setup helps in monitoring and debugging by providing detailed and structured logs for HTTP traffic.
 */
private fun Application.configureCallLogging() {
    val slf4jLevel = environment.config
        .propertyOrNull("ecomm.logging.level")
        ?.getString()
        ?.let { Level.valueOf(it) }
        ?: Level.INFO

    install(CallLogging) {
        level = slf4jLevel
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val ua = call.request.headers["User-Agent"]
            val ct = call.request.contentType()
            val length = call.request.headers["Content-Length"]
            "Request: $method $uri, Status: $status, Content-Type: $ct, Content-Length: $length, User-Agent: $ua"
        }
    }
}

/**
 * Configures response compression and caching headers for the application.
 *
 * This method installs:
 * - The `Compression` feature to optimize data transfer size by compressing HTTP responses
 * - The `ConditionalHeaders` feature to enable browser caching through ETags and Last-Modified headers
 *
 * The compression configuration includes:
 * - GZIP compression with priority 1.0 (highest)
 * - Minimum size threshold of 1 KB before compressing
 * - Content type matching for text and JSON responses
 *
 * Conditional headers are enabled to improve caching efficiency and reduce unnecessary transfers.
 */
private fun Application.configureCompressionAndCaching() {
    install(Compression) {
        gzip {
            priority = 1.0
            minimumSize(1_024)  // >1 KB
            matchContentType(ContentType.Text.Any, ContentType.Application.Json)
        }
    }
    install(ConditionalHeaders)
}

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
private fun Application.configureRouting() {
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

/**
 * Configures the database connection for the application and initializes the database schema.
 *
 * This method retrieves the database URL and driver details from the application's configuration settings.
 * It establishes a connection to the database and sets the default isolation level for transactions to `TRANSACTION_SERIALIZABLE`.
 * Additionally, it ensures the database schema is initialized and seeds the database with data if required.
 *
 * This function is typically invoked during the application startup process as part of configuring the application's resources.
 */
private fun Application.configureDatabase() {
    dependencies {
        provide<ProductRepository> { DatabaseProductRepositoryImpl }
    }
    val dbUrl = environment.config.property("ecomm.database.url").getString()
    val dbDriver = environment.config.property("ecomm.database.driver").getString()
    Database.connect(dbUrl, dbDriver)
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or configure this too if needed
    val imageUrlBase = environment.config.property("app.images.base-url").getString()
    initializeDatabaseAndSeedIfEmpty(imageUrlBase)
}

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
private fun Application.configureStatusPages() {
    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}