package io.aoriani

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import io.aoriani.io.aoriani.ecomm.graphql.queries.ProductQuery
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(GraphQL) {
        schema {
            packages = listOf("io.aoriani.ecomm.graphql.models")
            queries = listOf(ProductQuery())
        }
    }
    configureRouting()
    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}
