package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ProductListUiStateTest {

    @Test
    fun `When addToCart is called on Success state then the internal callback is invoked with the correct product`() {
        var wasCalled = false
        var capturedProduct: ProductBasic? = null
        val fakeProduct = object : ProductBasic {
            override val id: ProductBasic.Id = ProductBasic.Id("1")
            override val name: String = "Test Product"
            override val price: DollarAmount = DollarAmount("10.99")
            override val thumbnailUrl: String? = null
        }
        val successState = ProductListUiState.Success(persistentListOf(), _addToCart = {
            wasCalled = true
            capturedProduct = it
        })

        successState.addToCart(fakeProduct)

        assertTrue(wasCalled)
        assertSame(expected = fakeProduct, actual = capturedProduct)
    }

    @Test
    fun `When reload is called on Error state then the internal callback is invoked`() {
        var wasCalled = false
        val errorState = ProductListUiState.Error(_reload = {
            wasCalled = true
        })

        errorState.reload()

        assertTrue(wasCalled)
    }
}