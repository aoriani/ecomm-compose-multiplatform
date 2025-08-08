package dev.aoriani.ecomm.mcp

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProductMcpToolsTest {

    private fun getStringProperty(target: Any, propertyName: String): String? {
        val p = target::class.declaredMemberProperties.firstOrNull { it.name == propertyName }
        if (p != null) {
            p.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            return (p as KProperty1<Any, *>).get(target) as? String
        }
        return null
    }

    private val sampleFakeProducts: List<Product> = listOf(
        Product(
            id = ID("p1"),
            name = "Sample One",
            price = BigDecimal("10.00"),
            description = "First product",
            images = listOf("http://example.com/1.png"),
            material = "Cotton",
            inStock = true,
            countryOfOrigin = "US"
        ),
        Product(
            id = ID("p2"),
            name = "Sample Two",
            price = BigDecimal("20.00"),
            description = "Second product",
            images = listOf("http://example.com/2.png"),
            material = "Wool",
            inStock = false,
            countryOfOrigin = "CA"
        ),
    )

    private class FakeProductRepository(
        val get_all: () -> List<Product> = { emptyList() },
        val get_by_id: (String) -> Product? = { null }
    ) : ProductRepository {
        override suspend fun getAll(): List<Product> = get_all()
        override suspend fun getById(id: String): Product? = get_by_id(id)
    }

    @Test
    fun `When get_products_list is called then returns products array in structuredContent`() = runTest {
        // Arrange
        val repo = FakeProductRepository(
            get_all = { sampleFakeProducts },
            get_by_id = { id -> sampleFakeProducts.find { it.id.value == id } })
        val mcpTools = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)

        // Act
        val result = mcpTools.getProductsList(request)

        // Assert
        assertNotEquals(result.isError, true)
        assertEquals(sampleFakeProducts.size, result.content.size)
        // Unstructured content should include product names via toString()
        result.content.forEachIndexed { index, c ->
            val text = (c as TextContent).text
            assertEquals(text?.contains(sampleFakeProducts[index].name), true)
        }
        val expectedJson = buildJsonObject {
            put("products", Json.encodeToJsonElement(sampleFakeProducts))
        }
        assertEquals(expectedJson, result.structuredContent)
    }

    @Test
    fun `Tools definitions expose expected names`() {
        // Arrange
        val repo = FakeProductRepository()
        val mcpTools = ProductMcpTools(repo)

        // Act
        val toolDefs = mcpTools::class.declaredMemberProperties
            .filter { it.name.endsWith("ToolDef") }
            .onEach { it.isAccessible = true }
            .mapNotNull { it.getter.call(mcpTools) }

        val names = toolDefs.mapNotNull { getStringProperty(it, "name") }.toSet()

        // Assert
        assertEquals(setOf("get_products_list", "get_product"), names)
    }

    @Test
    fun `When inspecting get_products_list schema then products array items are object with properties`() {
        val repo = FakeProductRepository()
        val mcpTools = ProductMcpTools(repo)
        val outputSchemaProperties = mcpTools.getProductsListToolDef.outputSchema?.properties!!

        // Assert
        val productsSchema = outputSchemaProperties["products"]?.jsonObject
        assertNotNull(productsSchema)
        assertEquals("array", productsSchema["type"]?.jsonPrimitive?.content)

        val items = productsSchema["items"]?.jsonObject
        assertNotNull(items)
        assertEquals("object", items["type"]?.jsonPrimitive?.content)

        val itemProps = items["properties"]?.jsonObject
        assertNotNull(itemProps)
        assertTrue(
            setOf("id", "name", "price", "description", "images", "material", "inStock", "countryOfOrigin")
                .all { it in itemProps.keys }
        )
    }

    @Test
    fun `When installTools is invoked then both tools are registered on the server`() = runTest {
        // Arrange
        val repo = FakeProductRepository()
        val mcpTools = ProductMcpTools(repo)
        val server = mockk<Server>()
        val registered = mutableListOf<String>()
        every { server.addTool(any<Tool>(), any()) } answers {
            registered.add(firstArg<Tool>().name)
            Unit
        }

        // Act
        mcpTools.installTools(server)

        // Assert
        assertEquals(setOf("get_products_list", "get_product"), registered.toSet())
    }

    @Test
    fun `When inspecting get_product input schema then id is required and string`() {
        val repo = FakeProductRepository()
        val mcpTools = ProductMcpTools(repo)
        val toolDef = mcpTools.getProductToolDef

        assertEquals(toolDef.inputSchema.required?.contains("id"), true)
        assertEquals("string", toolDef.inputSchema.properties["id"]?.jsonObject["type"]?.jsonPrimitive?.content)
    }

    @Test
    fun `When get_product is called with existing id then returns that product`() = runTest {
        // Arrange
        val product = sampleFakeProducts.first()
        val targetId = product.id.value
        val repo =
            FakeProductRepository(
                get_all = { sampleFakeProducts },
                get_by_id = { sampleFakeProducts.find { it.id.value == targetId } })
        val mcpTools = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)
        every { request.arguments } returns buildJsonObject {
            put("id", JsonPrimitive(targetId))
        }

        // Act
        val result = mcpTools.getProduct(request)

        // Assert
        assertNotEquals(result.isError, true)
        assertEquals(1, result.content.size)
        // Unstructured content should include product name via toString()
        assertEquals(((result.content.first() as TextContent).text)?.contains(product.name), true)
        val expected = Json.encodeToJsonElement(product).jsonObject
        assertEquals(expected, result.structuredContent)
    }

    @Test
    fun `When get_product is called with missing id then returns error`() = runTest {
        // Arrange
        val repo = FakeProductRepository()
        val mcpTools = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)
        every { request.arguments } returns JsonObject(emptyMap())

        // Act
        val result = mcpTools.getProduct(request)

        // Assert
        assertEquals(result.isError, true)
        assertNull(result.structuredContent)
    }

    @Test
    fun `When get_product is called with unknown id then returns error`() = runTest {
        // Arrange
        val repo = FakeProductRepository(get_all = { sampleFakeProducts })
        val mcpTools = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)
        every { request.arguments } returns buildJsonObject {
            put("id", JsonPrimitive("does-not-exist"))
        }

        // Act
        val result = mcpTools.getProduct(request)

        // Assert
        assertEquals(result.isError, true)
        assertNull(result.structuredContent)
        assertNotNull(result.content)
    }
}
