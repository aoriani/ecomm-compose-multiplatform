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
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class McpToolTest {

    @Serializable
    class FakeInputClass(private val name: String, private val value: Int)

    @Serializable
    class FakeOutputClass(private val result: Boolean)

    @Serializable
    data class FakeOptionalInputClass(val maybe: String? = null)

    @Test
    fun `When addTool is called with tool having input and output then it registers with server`() {

        // Given
        val server = mockk<Server>(relaxed = true)
        val tool = createMockTool(
            name = "test_tool",
            description = "Test tool",
            input = FakeInputClass::class,
            output = FakeOutputClass::class
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
            output = FakeOutputClass::class
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
            input = FakeInputClass::class,
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
        val schema = FakeInputClass::class.toToolSchema()

        // Then
        assertNotNull(schema.properties)
        assertTrue(schema.properties.containsKey("name"))
        assertTrue(schema.properties.containsKey("value"))
        assertNotNull(schema.required)
        assertTrue(schema.required!!.contains("name"))
    }

    @Test
    fun `When toToolSchema is called for primitive type then it throws`() {
        assertFailsWith<IllegalArgumentException>(message = "schema does not contain properties") {
            String::class.toToolSchema()
        }
    }

    @Test
    fun `When class has only optional fields then required is null`() {
        val schema = FakeOptionalInputClass::class.toToolSchema()
        assertNotNull(schema.properties)
        assertTrue(schema.properties.containsKey("maybe"))
        assertNull(schema.required)
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
