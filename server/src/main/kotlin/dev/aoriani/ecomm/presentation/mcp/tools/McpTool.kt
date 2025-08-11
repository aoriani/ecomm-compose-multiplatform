package dev.aoriani.ecomm.presentation.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import kotlin.reflect.KClass

interface McpTool {
    val name: String
    val description: String
    val input: KClass<out Any>?
    val output: KClass<out Any>?

    suspend fun execute(request: CallToolRequest): CallToolResult
}