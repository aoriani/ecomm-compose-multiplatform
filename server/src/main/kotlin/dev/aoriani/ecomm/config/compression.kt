package dev.aoriani.ecomm.config

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.matchContentType
import io.ktor.server.plugins.compression.minimumSize

/**
 * Configure HTTP response compression.
 *
 * Installs Ktor's `Compression` plugin with:
 * - Gzip encoder (priority 1.0)
 * - Minimum body size of 1 KiB before compressing
 * - Content types limited to `text/ *` and `application/json`
 *
 * Negotiates compression based on the `Accept-Encoding` request header and
 * sets appropriate response headers (e.g., `Content-Encoding`, `Vary: Accept-Encoding`).
 * Small responses and non-matching content types are left uncompressed.
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
