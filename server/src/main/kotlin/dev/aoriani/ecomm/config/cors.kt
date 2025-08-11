package dev.aoriani.ecomm.config

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

/**
 * Default HTTP methods allowed by CORS.
 *
 * Used by the Ktor `CORS` plugin configuration to permit
 * cross-origin requests for: GET, POST, PUT, DELETE, PATCH, OPTIONS.
 */
// Extracted constants for reuse
private val ALLOWED_CORS_METHODS = listOf(
    HttpMethod.Get, HttpMethod.Post, HttpMethod.Put, HttpMethod.Delete,
    HttpMethod.Patch, HttpMethod.Options
)

/**
 * Request headers allowed in CORS requests.
 *
 * Keeps the set minimal while supporting common scenarios:
 * - `Content-Type` for JSON/form submissions
 * - `Authorization` for bearer tokens
 * - `X-Requested-With` for legacy/XHR clients
 */
private val ALLOWED_CORS_HEADERS = listOf(
    HttpHeaders.ContentType, HttpHeaders.Authorization, "X-Requested-With"
)

/**
 * Allowed origins by host and scheme.
 *
 * Keys are hostnames (optionally with port), values are allowed schemes
 * passed to `allowHost` (e.g., host "localhost:8080" with scheme "http").
 */
private val ALLOWED_CORS_HOSTS = mapOf(
    "localhost:8080" to listOf("http"),
    "aoriani.dev" to listOf("https"),
    "www.aoriani.dev" to listOf("https"),
    "api.aoriani.dev" to listOf("https")
)

/**
 * Configure Cross-Origin Resource Sharing (CORS).
 *
 * Installs Ktor's `CORS` plugin with:
 * - Allowed origins: specific hosts + schemes via `allowHost`.
 * - Allowed methods: GET, POST, PUT, DELETE, PATCH, OPTIONS.
 * - Allowed headers: `Content-Type`, `Authorization`, `X-Requested-With`.
 * - Credentials disabled: `allowCredentials = false` (cookies/auth not allowed cross-origin).
 *
 * The plugin automatically handles preflight (OPTIONS) requests and sets
 * the appropriate CORS response headers. Adjust hosts, methods, or headers
 * as needed for your deployment environments.
 */
internal fun Application.configureCors() {
    install(CORS) {
        ALLOWED_CORS_HOSTS.forEach { (host, schemes) -> allowHost(host, schemes = schemes) }
        ALLOWED_CORS_METHODS.forEach(::allowMethod)
        ALLOWED_CORS_HEADERS.forEach(::allowHeader)
        allowCredentials = false
    }
}
