package io.aoriani.ecomm.config

actual fun getGraphQLServerUrl(): String {
    // TODO: Implement proper configuration retrieval for iOS
    // For example, from a .plist file, build settings, or an injected value.
    return "https://aoriani.dev/graphql"
}
