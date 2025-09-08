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
}


