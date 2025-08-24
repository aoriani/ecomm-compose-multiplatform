package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ProductListUiStateTest {

    @Test
    fun `Given Loading state when cartItemCount is accessed then it returns the correct count`() {
        val expectedCartItemCount = 5
        val loadingState = ProductListUiState.Loading(cartItemCount = expectedCartItemCount)
        assertEquals(expected = expectedCartItemCount, actual = loadingState.cartItemCount)
    }

    @Test
    fun `Given Error state when cartItemCount is accessed then it returns the correct count`() {
        val expectedCartItemCount = 3
        val errorState = ProductListUiState.Error(cartItemCount = expectedCartItemCount, _reload = {})
        assertEquals(expected = expectedCartItemCount, actual = errorState.cartItemCount)
    }

    @Test
    fun `Given Success state when cartItemCount is accessed then it returns the correct count`() {
        val expectedCartItemCount = 10
        val successState = ProductListUiState.Success(
            products = persistentListOf(),
            cartItemCount = expectedCartItemCount,
            _addToCart = {}
        )
        assertEquals(expected = expectedCartItemCount, actual = successState.cartItemCount)
    }

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
        val successState = ProductListUiState.Success(
            products = persistentListOf(),
            cartItemCount = 0,
            _addToCart = {
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
        val errorState = ProductListUiState.Error(cartItemCount = 0, _reload = {
            wasCalled = true
        })

        errorState.reload()

        assertTrue(wasCalled)
    }
}