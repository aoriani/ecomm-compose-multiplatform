/**
 * MCP tool for retrieving a single product by its identifier.
 *
 * Exposes a tool named "get_product_by_id" that accepts an input payload containing
 * a product ID and returns the corresponding product details. Input and output schemas
 * are derived from the declared types and used by the MCP server for validation and discovery.
 */
package dev.aoriani.ecomm.presentation.mcp.tools

import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.mcp.models.GetProductByIdRequest
import dev.aoriani.ecomm.presentation.mcp.models.Product
import dev.aoriani.ecomm.presentation.mcp.models.toMcpProduct
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KClass

/**
 * Tool implementation that fetches a product by ID using a domain use case.
 *
 * @param getProductById Domain use case that retrieves a product by its unique identifier.
 */
class GetProductByIdTool(private val getProductById: GetProductByIdUseCase) : McpTool {
    override val name: String = "get_product_by_id"
    override val description: String = """
                    This tool retrieves details of a single product identified by a unique product ID. It takes the 
                    product ID as input and returns the corresponding product object.
                """.trimIndent()
    override val input: KClass<*> = GetProductByIdRequest::class
    override val output: KClass<*> = Product::class

    /**
     * Executes the tool call by validating the "id" argument, invoking the domain use case,
     * and returning both text and structured results.
     *
     * Error handling:
     * - Returns an error result if the "id" is missing or blank.
     * - Returns a generic error result if the domain use case fails.
     */
    override suspend fun execute(request: CallToolRequest): CallToolResult {
        // TODO: Add logging
        val args = runCatching { Json.decodeFromJsonElement<GetProductByIdRequest>(request.arguments) }.getOrNull()
        val id = args?.id
        if (id.isNullOrBlank()) return CallToolResult(
            content = listOf(TextContent("Product ID must not be blank")),
            isError = true
        )

        val result = getProductById(ProductId(id))
        return if (result.isSuccess) {
            val product = result.map { it.toMcpProduct() }.getOrThrow()
            CallToolResult(
                content = listOf(TextContent(product.toString())),
                structuredContent = Json.encodeToJsonElement(product).jsonObject
            )
        } else {
            // TODO: Log failure
            when (val exception = result.exceptionOrNull()) {
                is ProductNotFoundException -> CallToolResult(content = listOf(TextContent("Product not found: $id")), isError = true)
                else -> CallToolResult(content = listOf(TextContent("Unknown error")), isError = true)
            }
        }
    }
}