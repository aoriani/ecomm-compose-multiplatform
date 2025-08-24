package io.aoriani.ecomm.data.repositories.cart

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ZERO
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CartRepositoryImplTest {

    private val fakeProduct1 = object : ProductBasic {
        override val id: ProductBasic.Id = ProductBasic.Id("Product 1")
        override val name: String = "Product 1"
        override val price: DollarAmount = DollarAmount("10.00")
        override val thumbnailUrl: String? = null
    }

    private val fakeProduct2 = object : ProductBasic {
        override val id: ProductBasic.Id = ProductBasic.Id("Product 2")
        override val name: String = "Product 2"
        override val price: DollarAmount = DollarAmount("20.00")
        override val thumbnailUrl: String? = null
    }

    @Test
    fun `When initialized cart is empty`() {
        val cartRepository = CartRepositoryImpl()
        assertTrue(cartRepository.state.value.items.isEmpty())
        assertEquals(DollarAmount.ZERO, cartRepository.state.value.subTotal)
        assertEquals(0, cartRepository.state.value.count)
    }

    @Test
    fun `When adding a product it is added to the cart`() = runTest {
        val cartRepository = CartRepositoryImpl()
        cartRepository.add(fakeProduct1)
        val cartState = cartRepository.state.value
        assertEquals(1, cartState.items.size)
        assertEquals(1, cartState.count)
        assertEquals(DollarAmount("10.00"), cartState.subTotal)

        val firstCartItem = cartState.items.first()
        assertEquals(1, firstCartItem.quantity)
        assertEquals(fakeProduct1.id, firstCartItem.id)
        assertEquals(fakeProduct1.name, firstCartItem.name)
        assertEquals(fakeProduct1.price, firstCartItem.totalPrice)
    }

    @Test
    fun `When adding the same product twice there is a single entry in the cart`() = runTest {
        val cartRepository = CartRepositoryImpl()
        cartRepository.add(fakeProduct1)
        cartRepository.add(fakeProduct1)

        val cartState = cartRepository.state.value
        assertEquals(1, cartState.items.size)
        assertEquals(2, cartState.count)
        assertEquals(DollarAmount("20.00"), cartState.subTotal)

        val firstCartItem = cartState.items.first()
        assertEquals(2, firstCartItem.quantity)
        assertEquals(fakeProduct1.id, firstCartItem.id)
        assertEquals(fakeProduct1.name, firstCartItem.name)
        assertEquals(fakeProduct1.price * 2, firstCartItem.totalPrice)
    }

    @Test
    fun `When adding multiple products they are added to the cart`() = runTest {
        val cartRepository = CartRepositoryImpl()
        cartRepository.add(fakeProduct1)
        cartRepository.add(fakeProduct2)

        val cartState = cartRepository.state.value
        assertEquals(2, cartState.items.size)
        assertEquals(2, cartState.count)
        assertEquals(DollarAmount("30.00"), cartState.subTotal)

        val product1InCart = cartState.items.find { it.id == fakeProduct1.id }
        assertNotNull(product1InCart)
        assertEquals(1, product1InCart.quantity)
        assertEquals(fakeProduct1.id, product1InCart.id)
        assertEquals(fakeProduct1.name, product1InCart.name)
        assertEquals(fakeProduct1.price, product1InCart.totalPrice)

        val product2InCart = cartState.items.find { it.id == fakeProduct2.id }
        assertNotNull(product2InCart)
        assertEquals(1, product2InCart.quantity)
        assertEquals(fakeProduct2.id, product2InCart.id)
        assertEquals(fakeProduct2.name, product2InCart.name)
        assertEquals(fakeProduct2.price, product2InCart.totalPrice)
    }

    @Test
    fun `When updating the quantity of a product it is updated in the cart`() = runTest {
        val cartRepository = CartRepositoryImpl()
        cartRepository.add(fakeProduct1)

        var cartState = cartRepository.state.value
        assertEquals(1, cartState.items.size)
        assertEquals(1, cartState.count)
        assertEquals(DollarAmount("10.00"), cartState.subTotal)

        var firstCartItem = cartState.items.first()
        assertEquals(1, firstCartItem.quantity)
        assertEquals(fakeProduct1.id, firstCartItem.id)
        assertEquals(fakeProduct1.name, firstCartItem.name)
        assertEquals(fakeProduct1.price, firstCartItem.totalPrice)

        cartRepository.updateQuantity(productId = fakeProduct1.id, quantity = 3)

        cartState = cartRepository.state.value
        assertEquals(1, cartState.items.size)
        assertEquals(3, cartState.count)
        assertEquals(DollarAmount("30.00"), cartState.subTotal)

        firstCartItem = cartState.items.first()
        assertEquals(3, firstCartItem.quantity)
        assertEquals(fakeProduct1.id, firstCartItem.id)
        assertEquals(fakeProduct1.name, firstCartItem.name)
        assertEquals(fakeProduct1.price * 3, firstCartItem.totalPrice)
    }

    @Test
    fun `When clearing the cart it becomes empty`() = runTest {
        val cartRepository = CartRepositoryImpl()
        cartRepository.add(fakeProduct1)
        cartRepository.add(fakeProduct2)

        var cartState = cartRepository.state.value
        assertEquals(2, cartState.items.size)
        assertEquals(2, cartState.count)
        assertEquals(DollarAmount("30.00"), cartState.subTotal)

        cartRepository.remove(fakeProduct1.id)

        cartState = cartRepository.state.value
        assertEquals(1, cartState.items.size)
        assertEquals(1, cartState.count)
        assertEquals(DollarAmount("20.00"), cartState.subTotal)

        val firstCartItem = cartState.items.first()
        assertEquals(1, firstCartItem.quantity)
        assertEquals(fakeProduct2.id, firstCartItem.id)
        assertEquals(fakeProduct2.name, firstCartItem.name)
    }

    @Test
    fun `When removing a product it is removed from the cart`() = runTest {
        val cartRepository = CartRepositoryImpl()
        cartRepository.add(fakeProduct1)
        cartRepository.add(fakeProduct2)

        var cartState = cartRepository.state.value
        assertEquals(2, cartState.items.size)
        assertEquals(2, cartState.count)
        assertEquals(DollarAmount("30.00"), cartState.subTotal)

        cartRepository.clear()

        cartState = cartRepository.state.value
        assertEquals(0, cartState.items.size)
        assertEquals(0, cartState.count)
        assertEquals(DollarAmount.ZERO, cartState.subTotal)

    }
}