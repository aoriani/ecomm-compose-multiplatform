package dev.aoriani.ecomm.presentation.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult

interface McpTool {
    val name: String
    val description: String

    suspend fun execute(request: CallToolRequest): CallToolResult
}