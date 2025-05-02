package io.aoriani.ecomm.data.network

import com.apollographql.apollo.ApolloClient

fun ApolloClient(serverUrl: String): ApolloClient {
    return ApolloClient.Builder().serverUrl(serverUrl).build()
}