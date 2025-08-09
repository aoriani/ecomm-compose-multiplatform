package dev.aoriani.ecomm.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
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

    private lateinit var productRepository: ProductRepository
    private lateinit var productQuery: ProductQuery

    @BeforeTest
    fun setup() {
        productRepository = mockk()
        productQuery = ProductQuery(productRepository)
    }

    @Test
    fun `products should return all products from repository`() = runTest {
        val products = listOf(
            Product(
                ID("1"),
                "Product A",
                BigDecimal("10.0"),
                "Description A",
                listOf("imageA.jpg"),
                "Material A",
                true,
                "USA"
            ),
            Product(
                ID("2"),
                "Product B",
                BigDecimal("20.0"),
                "Description B",
                listOf("imageB.jpg"),
                "Material B",
                false,
                "Germany"
            )
        )
        coEvery { productRepository.getAll() } returns products

        val result = productQuery.products()

        assertEquals(products, result)
    }

    @Test
    fun `products should return empty list if no products in repository`() = runTest {
        coEvery { productRepository.getAll() } returns emptyList()

        val result = productQuery.products()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `product should return product by ID`() = runTest {
        val product = Product(
            ID("1"),
            "Product A",
            BigDecimal("10.0"),
            "Description A",
            listOf("imageA.jpg"),
            "Material A",
            true,
            "USA"
        )
        coEvery { productRepository.getById("1") } returns product

        val result = productQuery.product(ID("1"))

        assertEquals(product, result)
    }

    @Test
    fun `product should throw ProductNotFoundException if product not found`() = runTest {
        coEvery { productRepository.getById("nonexistent") } returns null

        assertFailsWith<ProductNotFoundException> {
            productQuery.product(ID("nonexistent"))
        }
    }

    @Test
    fun `product should throw IllegalArgumentException for blank ID`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            productQuery.product(ID(""))
        }
        assertFailsWith<IllegalArgumentException> {
            productQuery.product(ID("   "))
        }
    }
}
