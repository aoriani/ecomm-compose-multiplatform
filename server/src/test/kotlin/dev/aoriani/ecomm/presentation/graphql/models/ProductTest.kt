package dev.aoriani.ecomm.presentation.graphql.models

import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.domain.models.Product as DomainProduct
import dev.aoriani.ecomm.domain.models.ProductId
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductTest {

    @Test
    fun `When toGraphQlProduct is called then it correctly maps domain product to GraphQL product`() {
        // Given
        val domainProduct = DomainProduct(
            id = ProductId("test1"),
            name = "Test Product",
            price = BigDecimal("19.99"),
            description = "Test description",
            images = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
            material = "Test material",
            inStock = true,
            countryOfOrigin = "Test Country"
        )
        
        // When
        val graphQlProduct = domainProduct.toGraphQlProduct()
        
        // Then
        assertEquals(ID("test1"), graphQlProduct.id)
        assertEquals("Test Product", graphQlProduct.name)
        assertEquals(BigDecimal("19.99"), graphQlProduct.price)
        assertEquals("Test description", graphQlProduct.description)
        assertEquals(listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"), graphQlProduct.images)
        assertEquals("Test material", graphQlProduct.material)
        assertEquals(true, graphQlProduct.inStock)
        assertEquals("Test Country", graphQlProduct.countryOfOrigin)
    }
}