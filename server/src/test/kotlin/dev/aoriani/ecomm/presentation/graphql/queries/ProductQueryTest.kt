package dev.aoriani.ecomm.presentation.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.data.repositories.FakeProductRepository
import dev.aoriani.ecomm.domain.models.Product as DomainProduct
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.BlankProductIdException
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.graphql.exceptions.GraphQLInternalException
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ProductQueryTest {
    @Test
    fun `When products is called and use case returns success then it returns mapped products`() = runTest {
        // Given
        val domainProducts = listOf(
            DomainProduct(
                id = ProductId("test1"),
                name = "Test Product 1",
                price = BigDecimal("19.99"),
                description = "Test description 1",
                images = listOf("https://example.com/image1.jpg"),
                material = "Test material",
                inStock = true,
                countryOfOrigin = "Test Country"
            ),
            DomainProduct(
                id = ProductId("test2"),
                name = "Test Product 2",
                price = BigDecimal("29.99"),
                description = "Test description 2",
                images = listOf("https://example.com/image2.jpg"),
                material = "Test material 2",
                inStock = false,
                countryOfOrigin = "Test Country 2"
            )
        )

        val fakeProductRepository = FakeProductRepository(_getAll = { Result.success(domainProducts) })
        val getAllProductsUseCase = GetAllProductsUseCase(fakeProductRepository)
        val productQuery = ProductQuery(getAllProductsUseCase, GetProductByIdUseCase(fakeProductRepository))

        // When
        val result = productQuery.products()

        // Then
        assertEquals(2, result.size)
        assertEquals("Test Product 1", result[0].name)
        assertEquals(BigDecimal("19.99"), result[0].price)
        assertEquals(ID("test1"), result[0].id)
        assertEquals("Test Product 2", result[1].name)
    }

    @Test
    fun `When products is called and use case returns empty list then it returns empty list`() = runTest {
        // Given
        val fakeProductRepository = FakeProductRepository(_getAll = { Result.success(emptyList()) })
        val getAllProductsUseCase = GetAllProductsUseCase(fakeProductRepository)
        val productQuery = ProductQuery(getAllProductsUseCase, GetProductByIdUseCase(fakeProductRepository))

        // When
        val result = productQuery.products()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `When products is called and use case returns failure then it throws GraphQLInternalException`() = runTest {
        // Given
        val exception = Exception("Test exception")
        val fakeProductRepository = FakeProductRepository(_getAll = { Result.failure(exception) })
        val getAllProductsUseCase = GetAllProductsUseCase(fakeProductRepository)
        val productQuery = ProductQuery(getAllProductsUseCase, GetProductByIdUseCase(fakeProductRepository))

        // When/Then
        val thrownException = assertFailsWith<GraphQLInternalException> {
            productQuery.products()
        }

        // Then
        assertEquals("Failed to retrieve products", thrownException.message)
        assertEquals(exception, thrownException.cause)
    }


    @Test
    fun `When product is called with valid ID and use case returns success then it returns mapped product`() = runTest {
        // Given
        val productId = ProductId("test1")
        val domainProduct = DomainProduct(
            id = productId,
            name = "Test Product 1",
            price = BigDecimal("19.99"),
            description = "Test description 1",
            images = listOf("https://example.com/image1.jpg"),
            material = "Test material",
            inStock = true,
            countryOfOrigin = "Test Country"
        )

        val getProductByIdUseCase = GetProductByIdUseCase(FakeProductRepository(_getById = { Result.success(domainProduct) }))
        val productQuery = ProductQuery(GetAllProductsUseCase(FakeProductRepository()), getProductByIdUseCase)


        // When
        val result = productQuery.product(ID("test1"))

        // Then
        assertEquals("Test Product 1", result.name)
        assertEquals(BigDecimal("19.99"), result.price)
        assertEquals(ID("test1"), result.id)
    }

    @Test
    fun `When product is called and use case returns ProductNotFoundException then it throws the same exception`() = runTest {
        // Given
        val productId = ProductId("nonexistent")
        val exception = ProductNotFoundException(productId)
        val repository = FakeProductRepository(_getById = { Result.failure(exception) })
        val productQuery = ProductQuery(GetAllProductsUseCase(repository), GetProductByIdUseCase(repository))


        // When/Then
        val thrownException = assertFailsWith<ProductNotFoundException> {
            productQuery.product(ID("nonexistent"))
        }

        // Then
        assertEquals(exception, thrownException)
    }

    @Test
    fun `When product is called with blank ID then it throws BlankProductIdException`() = runTest {
        // This test is special because the BlankProductIdException is thrown during the creation of ProductId
        // So we need to test that the exception is propagated correctly
        val productQuery =
            ProductQuery(GetAllProductsUseCase(FakeProductRepository()), GetProductByIdUseCase(FakeProductRepository()))
        val thrownException = assertFailsWith<BlankProductIdException> {
            productQuery.product(ID(""))
        }

        // Then
        assertEquals("Product ID cannot be blank", thrownException.message)
    }

    @Test
    fun `When product is called and use case returns other exception then it throws GraphQLInternalException`() = runTest {
        // Given
        val productId = ProductId("test1")
        val exception = RuntimeException("Some other error")
        val repository = FakeProductRepository(_getById = { Result.failure(exception) })
        val productQuery = ProductQuery(GetAllProductsUseCase(repository), GetProductByIdUseCase(repository))


        // When/Then
        val thrownException = assertFailsWith<GraphQLInternalException> {
            productQuery.product(ID("test1"))
        }

        // Then
        assertEquals("Failed to retrieve product with id: test1", thrownException.message)
        assertEquals(exception, thrownException.cause)
    }
}