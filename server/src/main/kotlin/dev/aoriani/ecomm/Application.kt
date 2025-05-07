package dev.aoriani.ecomm

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import dev.aoriani.ecomm.graphql.queries.ProductQuery
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.util.logging.KtorSimpleLogger

internal val LOGGER = KtorSimpleLogger("com.example.RequestTracePlugin")

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        allowHost("localhost:8080", schemes = listOf("http"))
        allowHost("aoriani.dev", schemes = listOf("https"))

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("X-Requested-With") // se necessário

        allowCredentials = false // ou true, se estiver enviando cookies/autenticação
    }

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
