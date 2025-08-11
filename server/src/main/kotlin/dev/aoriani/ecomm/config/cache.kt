package dev.aoriani.ecomm.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.conditionalheaders.ConditionalHeaders


/**
 * Configure HTTP conditional requests (ETag/Last-Modified).
 *
 * Installs the Ktor `ConditionalHeaders` plugin which:
 * - Adds ETag and Last-Modified support where applicable
 * - Responds with 304 Not Modified when the resource hasn't changed
 * - Reduces bandwidth and speeds up repeat requests
 *
 * Note: This does not add Cache-Control headers. Use the `CachingHeaders`
 * plugin if you need explicit Cache-Control directives.
 */
internal fun Application.configureCaching() {
    install(ConditionalHeaders)
}
