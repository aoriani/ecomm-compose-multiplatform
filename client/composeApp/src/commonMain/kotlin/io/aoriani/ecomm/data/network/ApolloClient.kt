package io.aoriani.ecomm.data.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.ktorClient
import io.ktor.client.HttpClient

/**
 * Creates an [ApolloClient] instance configured with the given server URL and Ktor HTTP client.
 *
 * @param serverUrl The URL of the GraphQL server.
 * @param ktorClient The Ktor [HttpClient] to use for network requests.
 * @return A configured [ApolloClient] instance.
 */
fun ApolloClient(serverUrl: String, ktorClient: HttpClient): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl(serverUrl)
        .ktorClient(ktorClient)
        .build()
}