package dev.aoriani.ecomm.presentation.mcp.tools

import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import dev.aoriani.ecomm.domain.usecases.invoke
import dev.aoriani.ecomm.presentation.mcp.models.Products
import dev.aoriani.ecomm.presentation.mcp.models.toMcpProduct
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.KClass

class GetAllProductsTool(private val getAllProducts: GetAllProductsUseCase) : McpTool {
    override val name: String = "get_products_list"
    override val description: String = """
                    Retrieves all products in the catalog. Takes no input parameters and returns an object with a
                    'products' array, where each item is a product object as defined by the product schema.
                """.trimIndent()
    override val input: KClass<*>? = null
    override val output: KClass<*> = Products::class

    override suspend fun execute(request: CallToolRequest): CallToolResult {
        val result = getAllProducts()
        if (result.isFailure) {
            // TODO: Log failure
            return CallToolResult(
                content = listOf(TextContent("Unknown error")),
                isError = true
            )
        } else {
            val products = result.getOrNull()?.map { it.toMcpProduct() } ?: emptyList()
            val productsList = Products(products)
            val content = products.map { TextContent(it.toString()) }
            return CallToolResult(
                content = content,
                structuredContent = Json.encodeToJsonElement(productsList).jsonObject
            )
        }
    }
}