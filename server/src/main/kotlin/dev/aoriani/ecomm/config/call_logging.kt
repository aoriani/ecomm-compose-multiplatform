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
 * Configure structured HTTP request logging.
 *
 * Installs Ktor's `CallLogging` plugin with:
 * - Log level from `ecomm.logging.level` (defaults to `INFO`).
 * - Filter to log only requests whose path starts with '/'.
 * - Custom log line including method, request URI, response status, request Content-Type/Length, and User-Agent.
 *
 * Useful for monitoring and debugging without logging request/response bodies.
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
