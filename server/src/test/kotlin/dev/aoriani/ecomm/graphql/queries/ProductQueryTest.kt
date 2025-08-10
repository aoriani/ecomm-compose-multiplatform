package dev.aoriani.ecomm.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import dev.aoriani.ecomm.presentation.graphql.models.toGraphQlProduct
import dev.aoriani.ecomm.presentation.graphql.queries.ProductQuery
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest

class ProductQueryTest {

    private lateinit var mockGetAllProductsUseCase: GetAllProductsUseCase
    private lateinit var mockGetProductByIdUseCase: GetProductByIdUseCase
    private lateinit var mockProductRepository: ProductRepository
    private lateinit var mockProductQuery: ProductQuery

    @BeforeTest
    fun setup() {
        mockProductRepository = mockk()
        mockGetAllProductsUseCase = GetAllProductsUseCase(mockProductRepository)
        mockGetProductByIdUseCase = GetProductByIdUseCase(mockProductRepository)
        mockProductQuery = ProductQuery(
            getAllProducts = mockGetAllProductsUseCase,
            getProductById = mockGetProductByIdUseCase
        )
    }

    @Test
    fun `products should return all products from repository`() = runTest {
        val products = listOf(
            Product(
                id = "1",
                name = "Product A",
                price = BigDecimal("10.0"),
                description = "Description A",
                images = listOf("imageA.jpg"),
                material = "Material A",
                inStock = true,
                countryOfOrigin = "USA"
            ),
            Product(
                id = "2",
                name = "Product B",
                price = BigDecimal("20.0"),
                description = "Description B",
                images = listOf("imageB.jpg"),
                material = "Material B",
                inStock = false,
                countryOfOrigin = "Germany"
            )
        )
        coEvery { mockProductRepository.getAll() } returns Result.success(products)

        val result = mockProductQuery.products()

        assertEquals(products.map { it.toGraphQlProduct() }, result)
    }

    @Test
    fun `products should return empty list if no products in repository`() = runTest {
        coEvery { mockProductRepository.getAll() } returns Result.success(emptyList())

        val result = mockProductQuery.products()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `product should return product by ID`() = runTest {
        val product = Product(
            id = "1",
            name = "Product A",
            price = BigDecimal("10.0"),
            description = "Description A",
            images = listOf("imageA.jpg"),
            material = "Material A",
            inStock = true,
            countryOfOrigin = "USA"
        )
        coEvery { mockProductRepository.getById("1") } returns Result.success(product)

        val result = mockProductQuery.product(ID("1"))

        assertEquals(product.toGraphQlProduct(), result)
    }

    @Test
    fun `product should throw ProductNotFoundException if product not found`() = runTest {
        coEvery { mockProductRepository.getById("nonexistent") } returns Result.failure(ProductNotFoundException("nonexistent"))

        assertFailsWith<ProductNotFoundException> {
            mockProductQuery.product(ID("nonexistent"))
        }
    }

    @Test
    fun `product should throw IllegalArgumentException for blank ID`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            mockProductQuery.product(ID(""))
        }
        assertFailsWith<IllegalArgumentException> {
            mockProductQuery.product(ID("   "))
        }
    }
}
