package dev.aoriani.ecomm.mcp

import dev.aoriani.ecomm.repository.ProductRepository
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonObject

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

    private val getProductToolDef = Tool(
        name = "get_product",
        description = """
                    huhuhu
                """.trimIndent(),
        inputSchema = Tool.Input(properties = buildJsonObject {
            putJsonObject("id") {
                put("type", JsonPrimitive("string"))
                put("description", JsonPrimitive("Two-letter US state code (e.g. CA, NY)"))
            }
        }, required = listOf("id")),
        outputSchema = null,
        annotations = null,
    )


    fun installTools(mcpServer: Server) {
        mcpServer.addTool(getProductsToolDef, ::getProducts)
        mcpServer.addTool(getProductToolDef, ::getProduct)
    }

    private suspend fun getProducts(request: CallToolRequest): CallToolResult {
        val products = productRepository.getAll()
        val content = products.map { TextContent(it.toString()) }
        return CallToolResult(content = content)
    }

    private suspend fun getProduct(request: CallToolRequest): CallToolResult {
        val idArgument = request.arguments["id"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            ?: return CallToolResult(content = listOf(TextContent("id is required")), isError = true)
        val product = productRepository.getById(idArgument)
        return if (product == null) {
            CallToolResult(content = listOf(TextContent(product.toString())))
        } else {
            CallToolResult(content = listOf(TextContent("No product for id:=$idArgument")), isError = true)
        }

    }
}