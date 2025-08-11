package dev.aoriani.ecomm.config

import com.xemantic.ai.tool.schema.generator.generateSchema
import com.xemantic.ai.tool.schema.generator.jsonSchemaOf
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.mcp.tools.GetAllProductsTool
import dev.aoriani.ecomm.presentation.mcp.tools.GetProductByIdTool
import dev.aoriani.ecomm.presentation.mcp.tools.McpTool
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

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
            val getAllProductsUseCase: GetAllProductsUseCase by dependencies
            val getProductByIdUseCase: GetProductByIdUseCase by dependencies
            addTool(GetAllProductsTool(getAllProductsUseCase))
            addTool(GetProductByIdTool(getProductByIdUseCase))
        }
    }
}

private fun Server.addTool(tool: McpTool) {
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

private class ToolSchema(val properties: JsonObject, val required: List<String>? = null)

private fun KClass<out Any>.toToolSchema(): ToolSchema {
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
