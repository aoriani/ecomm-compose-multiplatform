package dev.aoriani.ecomm.mcp

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
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
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProductMcpToolsTest {

    // Helper to invoke private suspend handlers using reflection
    private suspend fun callHandler(
        target: Any,
        name: String,
        request: CallToolRequest
    ): CallToolResult {
        val fn = target::class.declaredMemberFunctions.first { it.name == name }
        fn.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return fn.callSuspend(target, request) as CallToolResult
    }

    private fun extractText(content: Any): String {
        return try {
            val prop = content::class.declaredMemberFunctions.firstOrNull { it.name == "getText" }
                ?: content::class.declaredMemberFunctions.firstOrNull { it.name == "component1" }
            if (prop != null) {
                prop.isAccessible = true
                (prop.call(content) as? String) ?: content.toString()
            } else {
                val textProp =
                    content::class.declaredMemberFunctions.firstOrNull { it.name.lowercase().contains("text") }
                if (textProp != null) {
                    textProp.isAccessible = true
                    (textProp.call(content) as? String) ?: content.toString()
                } else {
                    content.toString()
                }
            }
        } catch (_: Throwable) {
            content.toString()
        }
    }

    private fun getStringProp(target: Any, prop: String): String? {
        val capitalized = prop.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val getter = target::class.declaredMemberFunctions.firstOrNull { it.name == "get$capitalized" }
        if (getter != null) {
            getter.isAccessible = true
            return getter.call(target) as? String
        }
        val p = target::class.declaredMemberProperties.firstOrNull { it.name == prop }
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
        val sut = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)

        // Act
        val result = callHandler(sut, "getProductsList", request)

        // Assert
        assertNotEquals(result.isError, true)
        assertEquals(sampleFakeProducts.size, result.content.size)
        // Unstructured content should include product names via toString()
        result.content.forEachIndexed { index, c ->
            val text = extractText(c)
            kotlin.test.assertTrue(text.contains(sampleFakeProducts[index].name))
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
        val sut = ProductMcpTools(repo)

        // Act
        val toolDefs = sut::class.declaredMemberProperties
            .filter { it.name.endsWith("ToolDef") }
            .onEach { it.isAccessible = true }
            .mapNotNull { it.getter.call(sut) }

        val names = toolDefs.mapNotNull { getStringProp(it, "name") }.toSet()

        // Assert
        assertEquals(setOf("get_products_list", "get_product"), names)
    }

    @Test
    fun `When inspecting get_products_list schema then products array items are object with properties`() {
        // Arrange
        val repo = FakeProductRepository()
        val sut = ProductMcpTools(repo)

        // Act
        val listToolProp = sut::class.declaredMemberProperties.first { it.name == "getProductsListToolDef" }
        listToolProp.isAccessible = true
        val listToolDef = listToolProp.getter.call(sut)!!

        val outputSchemaProp = listToolDef::class.declaredMemberProperties.first { it.name == "outputSchema" }
        outputSchemaProp.isAccessible = true
        val outputSchema = outputSchemaProp.getter.call(listToolDef)!!

        val propertiesProp = outputSchema::class.declaredMemberProperties.first { it.name == "properties" }
        propertiesProp.isAccessible = true
        val properties = propertiesProp.getter.call(outputSchema) as JsonObject

        // Assert
        val productsSchema = properties["products"]?.jsonObject
        assertNotNull(productsSchema)
        assertEquals("array", productsSchema!!["type"]?.jsonPrimitive?.content)

        val items = productsSchema["items"]?.jsonObject
        assertNotNull(items)
        assertEquals("object", items!!["type"]?.jsonPrimitive?.content)

        val itemProps = items["properties"]?.jsonObject
        assertNotNull(itemProps)
        kotlin.test.assertTrue(
            setOf("id", "name", "price", "description", "images", "material", "inStock", "countryOfOrigin")
                .all { it in itemProps!!.keys }
        )
    }

    @Test
    fun `When installTools is invoked then both tools are registered on the server`() = runTest {
        // Arrange
        val repo = FakeProductRepository()
        val sut = ProductMcpTools(repo)
        val server = mockk<Server>()
        val registered = mutableListOf<String>()
        every { server.addTool(any<Tool>(), any()) } answers {
            registered.add(firstArg<Tool>().name)
            Unit
        }

        // Act
        sut.installTools(server)

        // Assert
        assertEquals(setOf("get_products_list", "get_product"), registered.toSet())
    }

    @Test
    fun `When inspecting get_product input schema then id is required and string`() {
        // Arrange
        val repo = FakeProductRepository()
        val sut = ProductMcpTools(repo)

        // Act
        val toolProp = sut::class.declaredMemberProperties.first { it.name == "getProductToolDef" }
        toolProp.isAccessible = true
        val toolDef = toolProp.getter.call(sut)!!

        val inputSchemaProp = toolDef::class.declaredMemberProperties.first { it.name == "inputSchema" }
        inputSchemaProp.isAccessible = true
        val inputSchema = inputSchemaProp.getter.call(toolDef)!!

        val requiredProp = inputSchema::class.declaredMemberProperties.firstOrNull { it.name == "required" }
        requiredProp!!.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val required = requiredProp.getter.call(inputSchema) as? List<String>

        val propertiesProp = inputSchema::class.declaredMemberProperties.firstOrNull { it.name == "properties" }
        propertiesProp!!.isAccessible = true
        val properties = propertiesProp.getter.call(inputSchema) as JsonObject

        // Assert
        kotlin.test.assertTrue(required!!.contains("id"))
        val idSchema = properties["id"]!!.jsonObject
        assertEquals("string", idSchema["type"]?.jsonPrimitive?.content)
    }

    @Test
    fun `When get_product is called with existing id then returns that product`() = runTest {
        // Arrange
        val products = sampleFakeProducts
        val targetId = products.first().id.value
        val repo =
            FakeProductRepository(get_all = { products }, get_by_id = { products.find { it.id.value == targetId } })
        val sut = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)
        every { request.arguments } returns buildJsonObject {
            put("id", JsonPrimitive(targetId))
        }

        // Act
        val result = callHandler(sut, "getProduct", request)

        // Assert
        assertNotEquals(result.isError, true)
        assertEquals(1, result.content.size)
        // Unstructured content should include product name via toString()
        kotlin.test.assertTrue(extractText(result.content.first()).contains(products.first().name))
        val expected = Json.encodeToJsonElement(products.first()).jsonObject
        assertEquals(expected, result.structuredContent)
    }

    @Test
    fun `When get_product is called with missing id then returns error`() = runTest {
        // Arrange
        val repo = FakeProductRepository()
        val sut = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)
        every { request.arguments } returns buildJsonObject { }

        // Act
        val result = callHandler(sut, "getProduct", request)

        // Assert
        assertEquals(result.isError, true)
        assertNull(result.structuredContent)
    }

    @Test
    fun `When get_product is called with unknown id then returns error`() = runTest {
        // Arrange
        val repo = FakeProductRepository(get_all = { sampleFakeProducts })
        val sut = ProductMcpTools(repo)
        val request = mockk<CallToolRequest>(relaxed = true)
        every { request.arguments } returns buildJsonObject {
            put("id", JsonPrimitive("does-not-exist"))
        }

        // Act
        val result = callHandler(sut, "getProduct", request)

        // Assert
        assertEquals(result.isError, true)
        assertNull(result.structuredContent)
        assertNotNull(result.content)
    }
}
