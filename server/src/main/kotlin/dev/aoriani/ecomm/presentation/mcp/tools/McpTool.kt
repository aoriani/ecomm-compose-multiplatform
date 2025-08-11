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

interface McpTool {
    val name: String
    val description: String
    val input: KClass<out Any>?
    val output: KClass<out Any>?

    suspend fun execute(request: CallToolRequest): CallToolResult
}

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

internal class ToolSchema(val properties: JsonObject, val required: List<String>? = null)

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