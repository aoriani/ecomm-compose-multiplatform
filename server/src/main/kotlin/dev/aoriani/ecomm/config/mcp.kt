package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.domain.repositories.ProductRepository
import dev.aoriani.ecomm.mcp.ProductMcpTools
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp

internal fun Application.configureMcp() {
    mcp {
        Server(
            serverInfo = Implementation("ecomm-mcp-server", "0.0.1"),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = null)
                )
            )
        ).apply {
            val repository: ProductRepository by dependencies
            ProductMcpTools(repository).installTools(this)
        }
    }
}
