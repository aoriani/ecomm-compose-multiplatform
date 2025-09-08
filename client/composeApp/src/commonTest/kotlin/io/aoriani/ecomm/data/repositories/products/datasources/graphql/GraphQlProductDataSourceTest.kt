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

    @Test
    fun `When server responds with bad request then fetchProducts fails`() = runTest {
        val apolloClient = mockApolloClient { respondBadRequest() }
        val dataSource = GraphQlProductDataSource(apolloClient)
        val result = dataSource.fetchProducts()
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When server responds with internal server error then fetchProducts fails`() = runTest {
        val apolloClient = mockApolloClient { respondError(HttpStatusCode.InternalServerError) }
        val dataSource = GraphQlProductDataSource(apolloClient)
        val result = dataSource.fetchProducts()
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When server socket timeouts then fetchProducts fails`() = runTest {
        val apolloClient =
            mockApolloClient { request -> throw HttpRequestTimeoutException(request) }
        val dataSource = GraphQlProductDataSource(apolloClient)
        val result = dataSource.fetchProducts()
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns errors body then fetchProducts fails`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": null, "errors":[{"message":"Internal server error"}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNull(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns data then fetchProducts succeeds`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{
                              "data": {
                                "products": [
                                  {
                                   "__typename": "Product",
                                    "id": "elon_musk_plush",
                                    "name": "Elon Musk",
                                    "price": 34.99,
                                    "images": [
                                      "https://api.aoriani.dev/static/images/elon_musk_plush.png"
                                    ]
                                  },
                                  {
                                    "__typename": "Product",
                                    "id": "steve_jobs_plush",
                                    "name": "Steve Jobs",
                                    "price": 39.99,
                                    "images": [
                                      "https://api.aoriani.dev/static/images/steve_jobs_plush.png"
                                    ]
                                  }
                                ]
                              }
                            }""".trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        val products = result.getOrNull()
        assertNotNull(products)
        assertEquals(2, products.size)
    }

    @Test
    fun `When server responds with bad request then getProduct fails`() = runTest {
        val apolloClient = mockApolloClient { respondBadRequest() }
        val dataSource = GraphQlProductDataSource(apolloClient)
        val result = dataSource.getProduct("id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When server responds with internal server error then getProduct fails`() = runTest {
        val apolloClient = mockApolloClient { respondError(HttpStatusCode.InternalServerError) }
        val dataSource = GraphQlProductDataSource(apolloClient)
        val result = dataSource.getProduct("id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When server socket timeouts then getProduct fails`() = runTest {
        val apolloClient =
            mockApolloClient { request -> throw HttpRequestTimeoutException(request) }
        val dataSource = GraphQlProductDataSource(apolloClient)
        val result = dataSource.getProduct("id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns errors body then getProduct fails`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": null, "errors":[{"message":"Internal server error"}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.getProduct("id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNull(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns data then getProduct succeeds`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{
                              "data": {
                                "product": {
                                  "__typename": "Product",
                                  "id": "elon_musk_plush",
                                  "name": "Elon Musk",
                                  "price": 34.99,
                                  "images": [
                                    "https://api.aoriani.dev/static/images/elon_musk_plush.png"
                                  ],
                                  "description": "This chibi-style plush of Elon Musk captures his visionary spirit with a mini SpaceX jacket and embroidered \nTesla T-shirt details. Crafted from an ultra-soft microfiber blend reminiscent of aerospace materials, it \nfeatures an oversized head, gentle pastel hues, and a tiny rocket accessory at his side. Perfect for fans \nof innovation, each stitch celebrates Musk’s journey from South Africa to the stars. A fun collector’s item \nand conversation starter for any tech enthusiast.",
                                  "material": "Aerospace-grade microfiber blend",
                                  "countryOfOrigin": "South Africa",
                                  "inStock": true
                                }
                              }
                            }""".trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.getProduct("id")
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `When GraphQL returns empty products list then fetchProducts succeeds with empty list`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{
                              "data": {
                                "products": []
                              }
                            }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        val products = result.getOrNull()
        assertNotNull(products)
        assertEquals(0, products.size)
    }

    @Test
    fun `When GraphQL returns null data for fetchProducts then returns empty list`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": null}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        val products = result.getOrNull()
        assertNotNull(products)
        assertEquals(0, products.size)
    }

    @Test
    fun `When GraphQL returns null product then getProduct fails with product not found error`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{
                              "data": {
                                "product": null
                              }
                            }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.getProduct("non-existent-id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("Error while reading JSON response") ?: false)
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns null data for getProduct then fails with product not found error`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": null}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.getProduct("non-existent-id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertTrue(result.exceptionOrNull()?.message?.contains("Product not found") ?: false)
        assertNull(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns multiple errors then fetchProducts fails with combined error messages`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": null, "errors":[{"message":"Error 1"}, {"message":"Error 2"}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue(errorMessage.contains("Error 1"))
        assertTrue(errorMessage.contains("Error 2"))
        assertNull(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns multiple errors then getProduct fails with combined error messages`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": null, "errors":[{"message":"Error 1"}, {"message":"Error 2"}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.getProduct("id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue(errorMessage.contains("Error 1"))
        assertTrue(errorMessage.contains("Error 2"))
        assertNull(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When fetchProducts succeeds then validates product data mapping`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{
                              "data": {
                                "products": [
                                  {
                                   "__typename": "Product",
                                    "id": "test_product",
                                    "name": "Test Product Name",
                                    "price": 29.99,
                                    "images": [
                                      "https://example.com/image1.png",
                                      "https://example.com/image2.png"
                                    ]
                                  }
                                ]
                              }
                            }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isSuccess)
        val products = result.getOrNull()
        assertNotNull(products)
        assertEquals(1, products.size)
        val product = products.first()
        assertEquals("test_product", product.id.value)
        assertEquals("Test Product Name", product.name)
        assertEquals("29.99", product.price.toString())
        assertEquals("https://example.com/image1.png", product.thumbnailUrl)
    }

    @Test
    fun `When getProduct succeeds then validates product data mapping`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{
                              "data": {
                                "product": {
                                  "__typename": "Product",
                                  "id": "detailed_product",
                                  "name": "Detailed Product",
                                  "price": 49.99,
                                  "images": [
                                    "https://example.com/detailed1.png",
                                    "https://example.com/detailed2.png"
                                  ],
                                  "description": "A detailed product description",
                                  "material": "Premium material",
                                  "countryOfOrigin": "USA",
                                  "inStock": false
                                }
                              }
                            }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.getProduct("detailed_product")
        assertTrue(result.isSuccess)
        val product = result.getOrNull()
        assertNotNull(product)
        assertEquals("detailed_product", product.id.value)
        assertEquals("Detailed Product", product.name)
        assertEquals("49.99", product.price.toString())
        assertEquals("A detailed product description", product.description)
        assertEquals("Premium material", product.material)
        assertEquals("USA", product.countryOfOrigin)
        assertEquals(false, product.inStock)
        assertEquals(2, product.images.size)
        assertTrue(product.images.contains("https://example.com/detailed1.png"))
        assertTrue(product.images.contains("https://example.com/detailed2.png"))
    }

    @Test
    fun `When fetchProducts receives product with no images then handles empty images gracefully`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{
                              "data": {
                                "products": [
                                  {
                                   "__typename": "Product",
                                    "id": "no_image_product",
                                    "name": "No Image Product",
                                    "price": 19.99,
                                    "images": []
                                  }
                                ]
                              }
                            }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isSuccess)
        val products = result.getOrNull()
        assertNotNull(products)
        assertEquals(1, products.size)
        val product = products.first()
        assertEquals("no_image_product", product.id.value)
        assertNull(product.thumbnailUrl)
    }

    @Test
    fun `When GraphQL returns malformed JSON then fetchProducts fails`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": {"products": [malformed json""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.fetchProducts()
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }

    @Test
    fun `When GraphQL returns malformed JSON then getProduct fails`() = runTest {
        val client = mockApolloClient {
            respond(
                content = """{"data": {"product": malformed json""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val dataSource = GraphQlProductDataSource(client)
        val result = dataSource.getProduct("id")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertIs<ProductRepository.ProductException>(result.exceptionOrNull())
        assertNotNull(result.exceptionOrNull()?.cause)
        assertIs<ApolloException>(result.exceptionOrNull()?.cause)
    }
}


