package dev.aoriani.ecomm

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import dev.aoriani.ecomm.graphql.ProductSchemaGeneratorHooks
import dev.aoriani.ecomm.graphql.queries.ProductQuery
import dev.aoriani.ecomm.repository.HardcodedProductRepositoryImpl
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.*
import org.slf4j.event.Level

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val uri = call.request.uri
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, URI: $uri, User agent: $userAgent"
        }
    }

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
            packages = listOf("dev.aoriani.ecomm.graphql.models", "java.math")
            queries = listOf(ProductQuery(HardcodedProductRepositoryImpl))
            hooks = ProductSchemaGeneratorHooks
        }
    }
    configureRouting()
    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}
