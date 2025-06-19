package io.aoriani.ecomm.config

import kotlinx.browser.window

actual fun getGraphQLServerUrl(): String {
    // TODO: Implement proper configuration retrieval for WasmJS
    // For example, from JavaScript window object, meta tags, or a fetched config file.
    // Consider security implications for web clients.
    // Example: return window.asDynamic().myappConfig.graphqlUrl ?: "https://aoriani.dev/graphql"
    return "https://aoriani.dev/graphql"
}
