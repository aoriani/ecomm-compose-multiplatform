package dev.aoriani.ecomm.mcp

import dev.aoriani.ecomm.repository.ProductRepository
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonObject
import org.jetbrains.annotations.VisibleForTesting

/**
 * MCP tools provider for product-related operations.
 *
 * @property productRepository the repository used to fetch product data.
 */
class ProductMcpTools(private val productRepository: ProductRepository) {

    /**
     * JSON schema describing a single product’s structure:
     * - id: unique string identifier
     * - name: product name
     * - price: numeric price value
     * - description: detailed text description
     * - images: list of image URL strings
     * - material: product material
     * - inStock: availability flag
     * - countryOfOrigin: manufacturing country
     */
    private val productSchema = buildJsonObject {
        put("id", buildJsonObject {
            put("type", JsonPrimitive("string"))
            put("description", JsonPrimitive("Unique identifier for the product."))
        })
        put("name", buildJsonObject {
            put("type", JsonPrimitive("string"))
            put("description", JsonPrimitive("Name of the product."))
        })
        put("price", buildJsonObject {
            put("type", JsonPrimitive("number"))
            put("format", JsonPrimitive("double"))
            put("description", JsonPrimitive("Unit price of the product."))
        })
        put("description", buildJsonObject {
            put("type", JsonPrimitive("string"))
            put("description", JsonPrimitive("Detailed text description of the product."))
        })
        put("images", buildJsonObject {
            put("type", JsonPrimitive("array"))
            put("items", buildJsonObject {
                put("type", JsonPrimitive("string"))
            })
            put("description", JsonPrimitive("Array of image URLs for the product."))
        })
        put("material", buildJsonObject {
            put("type", JsonPrimitive("string"))
            put("description", JsonPrimitive("Material the product is made of."))
        })
        put("inStock", buildJsonObject {
            put("type", JsonPrimitive("boolean"))
            put("description", JsonPrimitive("Indicates if the product is in stock."))
        })
        put("countryOfOrigin", buildJsonObject {
            put("type", JsonPrimitive("string"))
            put("description", JsonPrimitive("Country where the product is made."))
        })
    }

    /**
     * JSON schema for a list of products. The top-level output is an object
     * with a single property "products", which is an array of product objects
     * whose items conform to [productSchema].
     */
    private val productListSchema = buildJsonObject {
        put("products", buildJsonObject {
            put("type", JsonPrimitive("array"))
            // Each item is an object with the product properties
            put("items", buildJsonObject {
                put("type", JsonPrimitive("object"))
                put("properties", productSchema)
            })
        })
    }


    /**
     * Definition of the MCP “get_products_list” tool.
     * Fetches all available products. No input parameters.
     * Returns a JSON array of products matching [productListSchema].
     */
    @VisibleForTesting
    internal val getProductsListToolDef = Tool(
        name = "get_products_list",
        description = """
                    Retrieves all products in the catalog. Takes no input parameters and returns an object with a
                    'products' array, where each item is a product object as defined by the product schema.
                """.trimIndent(),
        inputSchema = Tool.Input(),
        outputSchema = Tool.Output(properties = productListSchema),
        annotations = null,
    )

    /**
     * Definition of the MCP “get_product” tool.
     * Retrieves a single product by its unique ID.
     *
     * Input:
     * - id (string): the product’s unique identifier.
     *
     * Output:
     * - a product object matching [productSchema], or an error if not found.
     */
    @VisibleForTesting
    internal val getProductToolDef = Tool(
        name = "get_product",
        description = """
                    This tool retrieves details of a single product identified by a unique product ID. It takes the 
                    product ID as input and returns the corresponding product object.
                """.trimIndent(),
        inputSchema = Tool.Input(properties = buildJsonObject {
            putJsonObject("id") {
                put("type", JsonPrimitive("string"))
                put("description", JsonPrimitive("The id of the product to retrieve."))
            }
        }, required = listOf("id")),
        outputSchema = Tool.Output(properties = productSchema),
        annotations = null,
    )


    /**
     * Registers the product tools with the given MCP server instance.
     *
     * @param mcpServer the server to which tools will be added.
     */
    fun installTools(mcpServer: Server) {
        mcpServer.addTool(getProductsListToolDef, ::getProductsList)
        mcpServer.addTool(getProductToolDef, ::getProduct)
    }

    /**
     * Handler for the “get_products_list” tool call.
     *
     * @param request the incoming tool request (ignored here).
     * @return a [CallToolResult] containing:
     *  - content: a list of text representations of all products.
     *  - structuredContent: JSON object with key "products" mapping to an array of product objects.
     */
    @VisibleForTesting
    internal suspend fun getProductsList(@Suppress("UNUSED_PARAMETER") request: CallToolRequest): CallToolResult {
        val products = productRepository.getAll()
        val content = products.map { TextContent(it.toString()) }
        return CallToolResult(
            content = content,
            structuredContent = buildJsonObject {
                put("products", Json.encodeToJsonElement(products))
            })
    }

    /**
     * Handler for the “get_product” tool call.
     *
     * @param request the incoming tool request containing argument “id”.
     * @return a [CallToolResult] containing either:
     *  - the matching product as text and JSON, or
     *  - an error message if the id is missing or not found.
     */
    @VisibleForTesting
    internal suspend fun getProduct(request: CallToolRequest): CallToolResult {
        return request.arguments["id"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { id ->
            productRepository.getById(id)?.let { product ->
                CallToolResult(
                    content = listOf(TextContent(product.toString())),
                    structuredContent = Json.encodeToJsonElement(product).jsonObject
                )
            } ?: CallToolResult(content = listOf(TextContent("No product for id:=$id")), isError = true)
        } ?: CallToolResult(content = listOf(TextContent("Product ID is missing in the request.")), isError = true)
    }
}
