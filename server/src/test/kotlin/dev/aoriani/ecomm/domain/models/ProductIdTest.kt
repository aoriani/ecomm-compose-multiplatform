package dev.aoriani.ecomm.domain.models

import dev.aoriani.ecomm.domain.models.exceptions.BlankProductIdException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProductIdTest {

    @Test
    fun `When id is blank then ProductId should throw BlankProductIdException`() {
        assertFailsWith<BlankProductIdException> {
            ProductId("")
        }
        assertFailsWith<BlankProductIdException> {
            ProductId("   ")
        }
    }

    @Test
    fun `When product id is not blank then ProductId shall wrap the id`() {
        val productId = ProductId("1")
        assertEquals("1", productId.id)
    }
}