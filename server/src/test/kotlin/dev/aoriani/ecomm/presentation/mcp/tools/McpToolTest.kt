package dev.aoriani.ecomm.presentation.mcp.tools

import io.mockk.mockk
import io.mockk.verify
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class McpToolTest {
    @Serializable
    class TestInput(private val name: String, private val value: Int)

    @Serializable
    class TestOutput(private val result: Boolean)

    @Test
    fun `When addTool is called with tool having input and output then it registers with server`() {

        // Given
        val server = mockk<Server>(relaxed = true)
        val tool = createMockTool(
            name = "test_tool",
            description = "Test tool",
            input = TestInput::class,
            output = TestOutput::class
        )

        // When
        server.addTool(tool)

        // Then
        verify {
            server.addTool(
                name = "test_tool",
                description = "Test tool",
                inputSchema = any(),
                outputSchema = any(),
                handler = any()
            )
        }
    }

    @Test
    fun `When addTool is called with tool having no input then it registers with empty input schema`() {
        // Given
        val server = mockk<Server>(relaxed = true)
        val tool = createMockTool(
            name = "test_tool",
            description = "Test tool",
            input = null,
            output = TestOutput::class
        )

        // When
        server.addTool(tool)

        // Then
        verify {
            server.addTool(
                name = "test_tool",
                description = "Test tool",
                inputSchema = match { it.properties == JsonObject(emptyMap()) && it.required == null },
                outputSchema = any(),
                handler = any()
            )
        }
    }

    @Test
    fun `When addTool is called with tool having no output then it registers with null output schema`() {
        // Given
        val server = mockk<Server>(relaxed = true)
        val tool = createMockTool(
            name = "test_tool",
            description = "Test tool",
            input = TestInput::class,
            output = null
        )

        // When
        server.addTool(tool)

        // Then
        verify {
            server.addTool(
                name = "test_tool",
                description = "Test tool",
                inputSchema = any(),
                outputSchema = null,
                handler = any()
            )
        }
    }

    @Test
    fun `When KClass toToolSchema is called then it extracts properties and required fields`() {
        // When
        val schema = TestInput::class.toToolSchema()

        // Then
        assertNotNull(schema.properties)
        assertTrue(schema.properties.containsKey("name"))
        assertTrue(schema.properties.containsKey("value"))
        assertNotNull(schema.required)
        assertTrue(schema.required!!.contains("name"))
    }

    @Test
    fun `When ToolSchema is created then it has correct properties and required fields`() {
        // Given
        val properties = JsonObject(mapOf("test" to JsonPrimitive("value")))
        val required = listOf("test")

        // When
        val schema = ToolSchema(properties, required)

        // Then
        assertEquals(properties, schema.properties)
        assertEquals(required, schema.required)
    }

    // Helper functions
    private fun createMockTool(
        name: String,
        description: String,
        input: KClass<*>?,
        output: KClass<*>?
    ): McpTool {
        return object : McpTool {
            override val name: String = name
            override val description: String = description
            override val input: KClass<out Any>? = input
            override val output: KClass<out Any>? = output

            override suspend fun execute(request: CallToolRequest): CallToolResult {
                return CallToolResult(emptyList())
            }
        }
    }
}