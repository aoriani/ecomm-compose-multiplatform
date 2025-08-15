package dev.aoriani.ecomm.domain.models.exceptions

import dev.aoriani.ecomm.domain.models.ProductId
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductNotFoundExceptionTest {

    @Test
    fun `ProductNotFoundException should have the correct message`() {
        val productId = ProductId("id")
        val exception = ProductNotFoundException(productId)
        assertEquals("Product with id '${productId.id}' not found.", exception.message)
    }

}