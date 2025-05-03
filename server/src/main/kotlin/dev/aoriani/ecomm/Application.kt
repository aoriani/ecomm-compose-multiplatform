package dev.aoriani.ecomm

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import dev.aoriani.ecomm.graphql.queries.ProductQuery
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.util.logging.KtorSimpleLogger

internal val LOGGER = KtorSimpleLogger("com.example.RequestTracePlugin")

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(GraphQL) {
        schema {
            packages = listOf("dev.aoriani.ecomm.graphql.models")
            queries = listOf(ProductQuery())
        }
    }
    configureRouting()
    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}
