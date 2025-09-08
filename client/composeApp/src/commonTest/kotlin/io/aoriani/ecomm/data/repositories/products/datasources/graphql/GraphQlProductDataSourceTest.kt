package io.aoriani.ecomm.data.repositories.products.datasources.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import io.aoriani.ecomm.data.network.ApolloClient
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphQlProductDataSourceTest {

    private fun mockApolloClient(responseHandler: MockRequestHandler): ApolloClient {
        val mockEngine = MockEngine { request ->
            responseHandler(request)
        }
        val ktorClient = HttpClient(mockEngine)
        return ApolloClient("https://example.com", ktorClient)
    }

    private fun createDataSourceWithResponse(responseHandler: MockRequestHandler): GraphQlProductDataSource {
        return GraphQlProductDataSource(mockApolloClient(responseHandler))
    }

    private fun createJsonResponse(content: String): MockRequestHandler = {
        respond(
            content = content,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    private fun assertFailureWithApolloException(result: Result<*>) {
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    private fun assertFailureWithoutCause(result: Result<*>) {
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNull(result.exceptionOrNull()?.cause)
    }

    private fun assertSuccessWithProductCount(result: Result<*>, expectedCount: Int) {
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        val products = result.getOrNull() as? List<*>
        assertNotNull(products)
        assertEquals(expectedCount, products.size)
    }

    private fun createErrorResponse(vararg errorMessages: String): String {
        val errors = errorMessages.joinToString(",") { """{"message":"$it"}""" }
        return """{"data": null, "errors":[$errors]}"""
    }

    private fun createProductsListResponse(vararg products: String): String {
        val productList = products.joinToString(",")
        return """{
                    "data": {
                      "products": [$productList]
                    }
                  }"""
    }

    private fun createSingleProductResponse(product: String): String {
        return """{
                    "data": {
                      "product": $product
                    }
                  }"""
    }

    private fun createBasicProduct(
        id: String,
        name: String,
        price: Double,
        vararg images: String
    ): String {
        val imageList = images.joinToString(",") { "\"$it\"" }
        return """{
                    "__typename": "Product",
                    "id": "$id",
                    "name": "$name",
                    "price": $price,
                    "images": [$imageList]
                  }"""
    }

    private fun createDetailedProduct(
        id: String, name: String, price: Double, description: String,
        material: String, countryOfOrigin: String, inStock: Boolean, vararg images: String
    ): String {
        val imageList = images.joinToString(",") { "\"$it\"" }
        return """{
                    "__typename": "Product",
                    "id": "$id",
                    "name": "$name",
                    "price": $price,
                    "images": [$imageList],
                    "description": "$description",
                    "material": "$material",
                    "countryOfOrigin": "$countryOfOrigin",
                    "inStock": $inStock
                  }"""
    }

    @Test
    fun `When server responds with bad request then fetchProducts fails`() = runTest {
        val dataSource = createDataSourceWithResponse { respondBadRequest() }
        val result = dataSource.fetchProducts()
        assertFailureWithApolloException(result)
    }

    @Test
    fun `When server responds with internal server error then fetchProducts fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse { respondError(HttpStatusCode.InternalServerError) }
        val result = dataSource.fetchProducts()
        assertFailureWithApolloException(result)
    }

    @Test
    fun `When server socket timeouts then fetchProducts fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse { request -> throw HttpRequestTimeoutException(request) }
        val result = dataSource.fetchProducts()
        assertFailureWithApolloException(result)
    }

    @Test
    fun `When GraphQL returns errors body then fetchProducts fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse(createJsonResponse(createErrorResponse("Internal server error")))
        val result = dataSource.fetchProducts()
        assertFailureWithoutCause(result)
    }

    @Test
    fun `When GraphQL returns data then fetchProducts succeeds`() = runTest {
        val product1 = createBasicProduct(
            "elon_musk_plush",
            "Elon Musk",
            34.99,
            "https://api.aoriani.dev/static/images/elon_musk_plush.png"
        )
        val product2 = createBasicProduct(
            "steve_jobs_plush",
            "Steve Jobs",
            39.99,
            "https://api.aoriani.dev/static/images/steve_jobs_plush.png"
        )
        val dataSource = createDataSourceWithResponse(
            createJsonResponse(
                createProductsListResponse(
                    product1,
                    product2
                )
            )
        )
        val result = dataSource.fetchProducts()
        assertSuccessWithProductCount(result, 2)
    }

    @Test
    fun `When server responds with bad request then getProduct fails`() = runTest {
        val dataSource = createDataSourceWithResponse { respondBadRequest() }
        val result = dataSource.getProduct("id")
        assertFailureWithApolloException(result)
    }

    @Test
    fun `When server responds with internal server error then getProduct fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse { respondError(HttpStatusCode.InternalServerError) }
        val result = dataSource.getProduct("id")
        assertFailureWithApolloException(result)
    }

    @Test
    fun `When server socket timeouts then getProduct fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse { request -> throw HttpRequestTimeoutException(request) }
        val result = dataSource.getProduct("id")
        assertFailureWithApolloException(result)
    }

    @Test
    fun `When GraphQL returns errors body then getProduct fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse(createJsonResponse(createErrorResponse("Internal server error")))
        val result = dataSource.getProduct("id")
        assertFailureWithoutCause(result)
    }

    @Test
    fun `When GraphQL returns data then getProduct succeeds`() = runTest {
        val product = createDetailedProduct(
            "elon_musk_plush", "Elon Musk", 34.99,
            """This chibi-style plush of Elon Musk captures his visionary spirit with a mini SpaceX jacket and embroidered 
Tesla T-shirt details. Crafted from an ultra-soft microfiber blend reminiscent of aerospace materials, it 
features an oversized head, gentle pastel hues, and a tiny rocket accessory at his side. Perfect for fans 
of innovation, each stitch celebrates Musk's journey from South Africa to the stars. A fun collector's item 
and conversation starter for any tech enthusiast.""".trimIndent(),
            "Aerospace-grade microfiber blend", "South Africa", true,
            "https://api.aoriani.dev/static/images/elon_musk_plush.png"
        )
        val dataSource =
            createDataSourceWithResponse(createJsonResponse(createSingleProductResponse(product)))
        val result = dataSource.getProduct("id")
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `When GraphQL returns empty products list then fetchProducts succeeds with empty list`() =
        runTest {
            val dataSource =
                createDataSourceWithResponse(createJsonResponse(createProductsListResponse()))
            val result = dataSource.fetchProducts()
            assertSuccessWithProductCount(result, 0)
        }

    @Test
    fun `When GraphQL returns null data for fetchProducts then returns empty list`() = runTest {
        val dataSource = createDataSourceWithResponse(createJsonResponse("""{"data": null}"""))
        val result = dataSource.fetchProducts()
        assertSuccessWithProductCount(result, 0)
    }

    @Test
    fun `When GraphQL returns null product then getProduct succeeds with null`() =
        runTest {
            val dataSource =
                createDataSourceWithResponse(createJsonResponse(createSingleProductResponse("null")))
            val result = dataSource.getProduct("non-existent-id")
            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }

    @Test
    fun `When GraphQL returns null data for getProduct then succeeds with null`() =
        runTest {
            val dataSource = createDataSourceWithResponse(createJsonResponse("""{"data": null}"""))
            val result = dataSource.getProduct("non-existent-id")
            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }

    @Test
    fun `When GraphQL returns multiple errors then fetchProducts fails with combined error messages`() =
        runTest {
            val dataSource = createDataSourceWithResponse(
                createJsonResponse(
                    createErrorResponse(
                        "Error 1",
                        "Error 2"
                    )
                )
            )
            val result = dataSource.fetchProducts()
            assertFailureWithoutCause(result)
            val errorMessage = result.exceptionOrNull()?.message ?: ""
            assertTrue(errorMessage.contains("Error 1"))
            assertTrue(errorMessage.contains("Error 2"))
        }

    @Test
    fun `When GraphQL returns multiple errors then getProduct fails with combined error messages`() =
        runTest {
            val dataSource = createDataSourceWithResponse(
                createJsonResponse(
                    createErrorResponse(
                        "Error 1",
                        "Error 2"
                    )
                )
            )
            val result = dataSource.getProduct("id")
            assertFailureWithoutCause(result)
            val errorMessage = result.exceptionOrNull()?.message ?: ""
            assertTrue(errorMessage.contains("Error 1"))
            assertTrue(errorMessage.contains("Error 2"))
        }

    @Test
    fun `When fetchProducts succeeds then validates product data mapping`() = runTest {
        val product = createBasicProduct(
            "test_product", "Test Product Name", 29.99,
            "https://example.com/image1.png", "https://example.com/image2.png"
        )
        val dataSource =
            createDataSourceWithResponse(createJsonResponse(createProductsListResponse(product)))
        val result = dataSource.fetchProducts()
        assertTrue(result.isSuccess)
        val products = result.getOrNull()
        assertNotNull(products)
        assertEquals(1, products.size)
        val productResult = products.first()
        assertEquals("test_product", productResult.id.value)
        assertEquals("Test Product Name", productResult.name)
        assertEquals("29.99", productResult.price.toString())
        assertEquals("https://example.com/image1.png", productResult.thumbnailUrl)
    }

    @Test
    fun `When getProduct succeeds then validates product data mapping`() = runTest {
        val product = createDetailedProduct(
            "detailed_product", "Detailed Product", 49.99,
            "A detailed product description", "Premium material", "USA", false,
            "https://example.com/detailed1.png", "https://example.com/detailed2.png"
        )
        val dataSource =
            createDataSourceWithResponse(createJsonResponse(createSingleProductResponse(product)))
        val result = dataSource.getProduct("detailed_product")
        assertTrue(result.isSuccess)
        val productResult = result.getOrNull()
        assertNotNull(productResult)
        assertEquals("detailed_product", productResult.id.value)
        assertEquals("Detailed Product", productResult.name)
        assertEquals("49.99", productResult.price.toString())
        assertEquals("A detailed product description", productResult.description)
        assertEquals("Premium material", productResult.material)
        assertEquals("USA", productResult.countryOfOrigin)
        assertEquals(false, productResult.inStock)
        assertEquals(2, productResult.images.size)
        assertTrue(productResult.images.contains("https://example.com/detailed1.png"))
        assertTrue(productResult.images.contains("https://example.com/detailed2.png"))
    }

    @Test
    fun `When fetchProducts receives product with no images then handles empty images gracefully`() =
        runTest {
            val product = createBasicProduct("no_image_product", "No Image Product", 19.99)
            val dataSource =
                createDataSourceWithResponse(createJsonResponse(createProductsListResponse(product)))
            val result = dataSource.fetchProducts()
            assertTrue(result.isSuccess)
            val products = result.getOrNull()
            assertNotNull(products)
            assertEquals(1, products.size)
            val productResult = products.first()
            assertEquals("no_image_product", productResult.id.value)
            assertNull(productResult.thumbnailUrl)
        }

    @Test
    fun `When GraphQL returns malformed JSON then fetchProducts fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse(createJsonResponse("""{"data": {"products": [malformed json"""))
        val result = dataSource.fetchProducts()
        assertFailureWithApolloException(result)
    }

    @Test
    fun `When GraphQL returns malformed JSON then getProduct fails`() = runTest {
        val dataSource =
            createDataSourceWithResponse(createJsonResponse("""{"data": {"product": malformed json"""))
        val result = dataSource.getProduct("id")
        assertFailureWithApolloException(result)
    }
}
