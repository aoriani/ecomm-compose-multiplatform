package dev.aoriani.ecomm.config

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.matchContentType
import io.ktor.server.plugins.compression.minimumSize

/**
 * Configures response compression.
 *
 * This method installs:
 * - The `Compression` feature to optimize data transfer size by compressing HTTP responses
 *
 * The compression configuration includes:
 * - GZIP compression with priority 1.0 (highest)
 * - Minimum size threshold of 1 KB before compressing
 * - Content type matching for text and JSON responses
 *
 */
internal fun Application.configureCompression() {
    install(Compression) {
        gzip {
            priority = 1.0
            minimumSize(1_024)  // >1 KB
            matchContentType(ContentType.Text.Any, ContentType.Application.Json)
        }
    }

}