package dev.aoriani.ecomm.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.contentType
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.uri
import org.slf4j.event.Level

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
internal fun Application.configureCallLogging() {
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