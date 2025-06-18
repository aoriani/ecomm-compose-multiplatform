package io.aoriani.ecomm.data.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.ktorClient
import io.ktor.client.HttpClient

fun ApolloClient(serverUrl: String, ktorClient: HttpClient): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl(serverUrl)
        .ktorClient(ktorClient)
        .build()
}