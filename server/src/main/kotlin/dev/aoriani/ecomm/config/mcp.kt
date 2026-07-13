package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.mcp.tools.GetAllProductsTool
import dev.aoriani.ecomm.presentation.mcp.tools.GetProductByIdTool
import dev.aoriani.ecomm.presentation.mcp.tools.addTool
import io.ktor.server.application.Application
import io.ktor.server.config.getAs
import io.ktor.server.plugins.di.dependencies
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcpStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities

/**
 * Configures and initializes the MCP (Message Communication Protocol) server within the application.
 *
 * This setup includes:
 * - Deserializing the `ecomm.mcp` configuration section into typed settings.
 * - Setting up the server with the provided name, version, and capabilities.
 * - Registering the available tools (`GetAllProductsTool` and `GetProductByIdTool`) with the server,
 *   enabling handling of respective MCP requests.
 *
 * The tools leverage dependency injection to access their respective use cases:
 * - `GetAllProductsUseCase`: Retrieves all available products.
 * - `GetProductByIdUseCase`: Retrieves details of a specific product by its unique identifier.
 */
internal fun Application.configureMcp() {
    val settings = environment.config.property("ecomm.mcp").getAs<McpSettings>()
    mcpStreamableHttp(
        path = "/mcp",
        allowedHosts = settings.allowedHosts,
        allowedOrigins = settings.allowedOrigins,
    ) {
        Server(
            serverInfo = Implementation(settings.serverName, settings.serverVersion),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = null)
                )
            )
        ).apply {
            val getAllProductsUseCase: GetAllProductsUseCase by dependencies
            val getProductByIdUseCase: GetProductByIdUseCase by dependencies
            addTool(GetAllProductsTool(getAllProductsUseCase))
            addTool(GetProductByIdTool(getProductByIdUseCase))
        }
    }
}
