package dev.aoriani.ecomm.presentation.mcp.tools

import com.xemantic.ai.tool.schema.generator.generateSchema
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

/**
 * Contract for a Model Context Protocol (MCP) tool.
 *
 * An implementation describes a tool's identity (name and description), the optional
 * input and output types used for schema generation, and the execution handler that
 * processes a [CallToolRequest] and returns a [CallToolResult].
 *
 * The [input] and [output] types, when provided, are used to derive JSON Schemas
 * that are registered with the MCP server for validation and client introspection.
 *
 * @property name Unique tool name as exposed to the MCP server.
 * @property description Human-readable description of what the tool does.
 * @property input Optional KClass describing the input payload type for the tool.
 * @property output Optional KClass describing the output payload type for the tool.
 */
interface McpTool {
    val name: String
    val description: String
    val input: KClass<out Any>?
    val output: KClass<out Any>?

    /**
     * Executes the tool logic.
     *
     * @param request The invocation context and payload from the MCP server.
     * @return The result to be sent back to the caller.
     */
    suspend fun execute(request: CallToolRequest): CallToolResult
}

/**
 * Registers a [McpTool] on this MCP [Server].
 *
 * This function derives JSON Schemas for the tool's input and output (when types
 * are provided), wires the tool's execution handler, and exposes the tool metadata
 * to the server so that clients can discover and invoke it.
 *
 * @param tool The tool instance to register.
 */
internal fun Server.addTool(tool: McpTool) {
    val toolInputSchema = tool.input?.toToolSchema()
    val toolOutputSchema = tool.output?.toToolSchema()

    addTool(
        name = tool.name,
        description = tool.description,
        inputSchema = toolInputSchema?.let { Tool.Input(it.properties, it.required) } ?: Tool.Input(),
        outputSchema = toolOutputSchema?.let { Tool.Output(it.properties, it.required) },
        handler = tool::execute,
    )
}

/**
 * Holds the subset of a JSON Schema required by the MCP SDK to describe a tool's
 * input or output payload.
 *
 * @property properties The JSON object containing property schemas.
 * @property required The list of required property names, if any.
 */
internal class ToolSchema(val properties: JsonObject, val required: List<String>? = null)

/**
 * Builds a [ToolSchema] for this class using kotlinx.serialization and a JSON Schema generator.
 *
 * The function creates a serializer for the class' [kotlin.reflect.KType], generates a JSON Schema,
 * and extracts the "properties" and "required" sections used by the MCP SDK.
 *
 * @return A [ToolSchema] with the extracted properties and required fields.
 * @throws IllegalArgumentException if the generated schema does not contain a "properties" object.
 */
internal fun KClass<out Any>.toToolSchema(): ToolSchema {
    // Build a serializer for the KType, mirroring jsonSchemaOf’s internals
    val ktype = this.createType()
    val kserializer = serializer(ktype)
    val jsonSchema = generateSchema(
        descriptor = kserializer.descriptor,
        inlineRefs = true
    )
    val jsonObjectSchema = Json.encodeToJsonElement(jsonSchema).jsonObject
    val schema = requireNotNull(jsonObjectSchema["properties"]?.jsonObject) { "schema does not contain properties" }
    val required = jsonObjectSchema["required"]?.jsonArray?.map { it.jsonPrimitive.content }
    return ToolSchema(schema, required)
}