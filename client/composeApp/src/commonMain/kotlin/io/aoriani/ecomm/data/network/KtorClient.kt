package io.aoriani.ecomm.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import co.touchlab.kermit.Logger as KermitLogger

/**
 * Creates and configures a Ktor HttpClient instance.
 *
 * This function sets up an HttpClient with the Logging plugin installed.
 * The logging level is set to LogLevel.ALL, meaning all network requests and responses
 * will be logged.
 * A custom logger is provided that uses KermitLogger with the tag "Network"
 * to output log messages at the debug level.
 *
 * The `@Suppress("FunctionName")` annotation is used because Kotlin convention
 * suggests that factory functions for classes should have the same name as the class
 * they are creating (e.g., `HttpClient()`), but here we are providing a configured
 * instance, so a more descriptive name like `createConfiguredHttpClient` might be
 * conventional. However, `KtorClient` is used for brevity and clarity within this project's context.
 *
 * @return A configured [HttpClient] instance.
 */
@Suppress("FunctionName")
fun KtorClient() = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                KermitLogger.withTag("Network").d(message)
            }
        }
    }
}