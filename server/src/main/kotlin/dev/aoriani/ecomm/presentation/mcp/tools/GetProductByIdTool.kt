package dev.aoriani.ecomm.presentation.mcp.tools

import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.mcp.models.GetProductByIdRequest
import dev.aoriani.ecomm.presentation.mcp.models.Product
import dev.aoriani.ecomm.presentation.mcp.models.toMcpProduct
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KClass

class GetProductByIdTool(private val getProductById: GetProductByIdUseCase) : McpTool {
    override val name: String = "get_product_by_id"
    override val description: String = """
                    This tool retrieves details of a single product identified by a unique product ID. It takes the 
                    product ID as input and returns the corresponding product object.
                """.trimIndent()
    override val input: KClass<*> = GetProductByIdRequest::class
    override val output: KClass<*> = Product::class

    override suspend fun execute(request: CallToolRequest): CallToolResult {
        // TODO: Add logging
        val id = request.arguments["id"]?.jsonPrimitive?.contentOrNull
        if (id.isNullOrBlank()) return CallToolResult(
            content = listOf(TextContent("Product ID must not be blank")),
            isError = true
        )

        val result = getProductById(id)
        if (result.isSuccess) {
            val product = result.map { it.toMcpProduct() }.getOrThrow()
            return CallToolResult(
                content = listOf(TextContent(product.toString())),
                structuredContent = Json.encodeToJsonElement(product).jsonObject
            )
        } else {
            // TODO: Log failure
            return CallToolResult(content = listOf(TextContent("Unknown error")), isError = true)
        }
    }
}