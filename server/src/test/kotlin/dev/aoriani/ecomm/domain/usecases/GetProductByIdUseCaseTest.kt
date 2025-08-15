package dev.aoriani.ecomm.domain.usecases

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GetProductByIdUseCaseTest {

    /**
     * Test class for `GetProductByIdUseCase`.
     *
     * The `GetProductByIdUseCase` class is responsible for fetching a product by its ID
     * from a `ProductRepository`. It takes a `ProductId` as input and returns a `Result`
     * that encapsulates either the requested `Product` or an error state.
     */

    @Test
    fun `When item is found on repository it shall return success`() = runTest {
        // Arrange
        val productId = ProductId("123")
        val product = Product(
            id = productId,
            name = "Product 1",
            price = BigDecimal("19.99"),
            description = "Some description",
            images = listOf("https://example.com/image1.jpg"),
            material = "Cotton",
            inStock = true,
            countryOfOrigin = "US"
        )
        val repository = mockk<ProductRepository>()
        coEvery { repository.getById(productId) } returns Result.success(product)
        val useCase = GetProductByIdUseCase(repository)

        // Act
        val result = useCase(productId)

        // Assert
        coVerify(exactly = 1){ repository.getById(productId) }
        assertTrue(result.isSuccess)
        assertEquals(product, result.getOrNull())
    }

    @Test
    fun `When item is not found in repository it will fail with ProductNotFoundException`() = runTest {
        // Arrange
        val productId = ProductId("123")
        val repository = mockk<ProductRepository>()
        coEvery { repository.getById(productId) } returns Result.failure(ProductNotFoundException(productId))
        val useCase = GetProductByIdUseCase(repository)

        // Act
        val result = useCase(productId)

        // Assert
        coVerify(exactly = 1){ repository.getById(productId) }
        assertTrue(result.isFailure)
        assertIs<ProductNotFoundException>(result.exceptionOrNull())
    }

    @Test
    fun `When repository report errors so does the use case`() = runTest {
        // Arrange
        val productId = ProductId("id")
        val repository = mockk<ProductRepository>()
        coEvery { repository.getById(productId) } returns Result.failure(Exception("Invalid Product ID"))
        val useCase = GetProductByIdUseCase(repository)

        // Act
        val result = useCase(productId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid Product ID", result.exceptionOrNull()?.message)
    }
}