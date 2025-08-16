package dev.aoriani.ecomm.domain.usecases

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.Result
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class GetAllProductsUseCaseTest {

    /**
     * `GetAllProductsUseCase` is responsible for retrieving all products from a repository.
     * It invokes the `getAll` method of the `ProductRepository` to fetch a list of products.
     * The tests verify different scenarios for fetching these products.
     */

    @Test
    fun `When repository returns successful result use case should return list of products `() = runTest {
        // Arrange
        val repository = mockk<ProductRepository>()
        val expectedProducts = listOf(
            Product(
                id = ProductId("1"),
                name = "Product 1",
                price = BigDecimal("19.99"),
                description = "Some description",
                images = listOf("https://example.com/image1.jpg"),
                material = "Cotton",
                inStock = true,
                countryOfOrigin = "US"
            ), Product(
                id = ProductId("2"),
                name = "Product 2",
                price = BigDecimal("0.00"),
                description = "Placeholder description",
                images = emptyList(),
                material = "Unknown",
                inStock = false,
                countryOfOrigin = "US"
            )
        )
        coEvery { repository.getAll() } returns Result.success(expectedProducts)
        val useCase = GetAllProductsUseCase(repository)

        // Act
        val result = useCase.invoke(Unit)

        // Assert
        coVerify(exactly = 1) { repository.getAll() }
        assertFalse(result.isFailure)
        assertEquals(Result.success(expectedProducts), result)
    }

    @Test
    fun `When repository returns has no product use case returns empty list`() = runTest {
        // Arrange
        val repository = mockk<ProductRepository>()
        val expectedProducts = emptyList<Product>()
        coEvery { repository.getAll() } returns Result.success(expectedProducts)
        val useCase = GetAllProductsUseCase(repository)

        // Act
        val result = useCase()

        // Assert
        assertEquals(Result.success(expectedProducts), result)
    }

    @Test
    fun `When repository fails use case returns failure`() = runTest {
        // Arrange
        val repository = mockk<ProductRepository>()
        val expectedError = Exception("Repository failure")
        coEvery { repository.getAll() } returns Result.failure(expectedError)
        val useCase = GetAllProductsUseCase(repository)

        // Act
        val result = useCase.invoke(Unit)

        // Assert
        assertEquals(Result.failure(expectedError), result)
    }
}