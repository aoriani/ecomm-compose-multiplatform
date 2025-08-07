package dev.aoriani.ecomm.mcp

import dev.aoriani.ecomm.repository.ProductRepository
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.Json

class ProductMcpTools(private val productRepository: ProductRepository) {

    private val getProductsToolDef = Tool(
        name = "get_products",
        description = """
                    hjhj
                """.trimIndent(),
        inputSchema = Tool.Input(),
        outputSchema = null,
        annotations = null,
    )


    fun installTools(mcpServer: Server) {
        mcpServer.addTool(getProductsToolDef, ::getProducts)
    }

    private suspend fun getProducts(request: CallToolRequest): CallToolResult {
        val products = productRepository.getAll()
        val content = products.map { TextContent(it.toString()) }
        return CallToolResult(content = content)
    }
}