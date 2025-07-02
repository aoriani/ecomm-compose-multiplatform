package dev.aoriani.ecomm

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import dev.aoriani.ecomm.graphql.ProductSchemaGeneratorHooks
import dev.aoriani.ecomm.graphql.queries.ProductQuery
import dev.aoriani.ecomm.repository.HardcodedProductRepositoryImpl
import dev.aoriani.ecomm.repository.database.DatabaseProductRepositoryImpl
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

/**
 * Main Ktor application module.
 * Configures plugins, database, GraphQL, routing, and other application services.
 */
fun Application.module() {
    configureCallLogging()
    configureDatabase()
    configureCors()
    configureGraphQl()
    configureRouting()
    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}

private fun Application.configureGraphQl() {
    install(GraphQL) {
        schema {
            packages = listOf("dev.aoriani.ecomm.graphql.models", "java.math")
            queries = listOf(ProductQuery(DatabaseProductRepositoryImpl))
            hooks = ProductSchemaGeneratorHooks
        }
    }
}

private fun Application.configureCors() {
    install(CORS) {
        // Common local development ports for client applications
        allowHost("localhost:8080", schemes = listOf("http"))
        // Production client host
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
}

private fun Application.configureCallLogging() {
    val callLogLevel = environment.config.propertyOrNull("ecomm.logging.level")?.getString()
    val slf4jLevel = Level.valueOf(callLogLevel ?: "INFO") // Default to INFO if not set

    install(CallLogging) {
        level = slf4jLevel
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val uri = call.request.uri
            val userAgent = call.request.headers["User-Agent"]
            val contentType = call.request.contentType()
            val contentLength = call.request.headers["Content-Length"]
            "Request: $httpMethod $uri, Status: $status, Content-Type: $contentType, Content-Length: $contentLength, User-Agent: $userAgent"
        }
    }
}
