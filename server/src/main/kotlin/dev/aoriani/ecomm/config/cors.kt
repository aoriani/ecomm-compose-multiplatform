package dev.aoriani.ecomm.config

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

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
    "aoriani.dev" to listOf("https"),
    "www.aoriani.dev" to listOf("https"),
    "api.aoriani.dev" to listOf("https")
)

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
internal fun Application.configureCors() {
    install(CORS) {
        ALLOWED_CORS_HOSTS.forEach { (host, schemes) -> allowHost(host, schemes = schemes) }
        ALLOWED_CORS_METHODS.forEach(::allowMethod)
        ALLOWED_CORS_HEADERS.forEach(::allowHeader)
        allowCredentials = false
    }
}