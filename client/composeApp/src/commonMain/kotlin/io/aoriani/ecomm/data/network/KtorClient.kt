package io.aoriani.ecomm.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import co.touchlab.kermit.Logger as KermitLogger

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