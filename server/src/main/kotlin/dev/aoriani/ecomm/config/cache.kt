package dev.aoriani.ecomm.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.conditionalheaders.ConditionalHeaders

/**
 * Configures caching headers for the application.
 *
 * This method installs:
 * - The `ConditionalHeaders` feature to enable browser caching through ETags and Last-Modified headers
 *
 * Conditional headers are enabled to improve caching efficiency and reduce unnecessary transfers.
 */
internal fun Application.configureCaching() {
    install(ConditionalHeaders)
}