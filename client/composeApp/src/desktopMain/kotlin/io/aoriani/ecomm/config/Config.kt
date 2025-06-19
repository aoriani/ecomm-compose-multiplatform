package io.aoriani.ecomm.config

actual fun getGraphQLServerUrl(): String {
    // TODO: Implement proper configuration retrieval for Desktop
    // For example, from environment variables, a properties file, or command-line arguments.
    return "https://aoriani.dev/graphql"
}
