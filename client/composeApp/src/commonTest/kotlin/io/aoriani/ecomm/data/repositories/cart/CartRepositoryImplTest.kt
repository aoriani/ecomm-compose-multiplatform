package io.aoriani.ecomm.data.repositories.cart

import io.aoriani.ecomm.data.model.CartItem
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ZERO
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
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

    private lateinit var cartRepository: CartRepositoryImpl

    @BeforeTest
    fun setUp() {
        cartRepository = CartRepositoryImpl()
    }

    @Test
    fun `When initialized cart is empty`() {
        assertCartState(expectedSize = 0, expectedCount = 0, expectedSubTotal = DollarAmount.ZERO)
    }

    @Test
    fun `When adding a product it is added to the cart`() = runTest {
        cartRepository.add(fakeProduct1)
        assertCartState(
            expectedSize = 1,
            expectedCount = 1,
            expectedSubTotal = DollarAmount("10.00")
        )

        val firstCartItem = cartRepository.state.value.items.first()
        assertCartItem(
            cartItem = firstCartItem,
            expectedProduct = fakeProduct1,
            expectedQuantity = 1
        )
    }

    @Test
    fun `When adding the same product twice there is a single entry in the cart`() = runTest {
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
    fun `When removing a product it is removed from the cart`() = runTest {
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
    fun `When clearing the cart it becomes empty`() = runTest {
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

    private fun assertCartState(
        expectedSize: Int,
        expectedCount: Int,
        expectedSubTotal: DollarAmount,
        cartState: CartRepository.State = cartRepository.state.value // Optional default
    ) {
        assertEquals(expectedSize, cartState.items.size, "Cart items size mismatch")
        assertEquals(expectedCount, cartState.count, "Cart count mismatch")
        assertEquals(expectedSubTotal, cartState.subTotal, "Cart subtotal mismatch")
    }

    private fun assertCartItem(
        cartItem: CartItem?, // Make it nullable and assertNotNull inside
        expectedProduct: ProductBasic,
        expectedQuantity: Int
    ) {
        assertNotNull(cartItem, "Cart item for product ${expectedProduct.id.value} should not be null")
        cartItem.run { // Safe call after assertNotNull
            assertEquals(expected = expectedQuantity,
                actual = quantity,
                message = "Item quantity mismatch"
            )
            assertEquals(expected = expectedProduct.id, actual = id, message = "Item ID mismatch")
            assertEquals(
                expected = expectedProduct.name,
                actual = name,
                message = "Item name mismatch"
            )
            assertEquals(
                expected = expectedProduct.price * expectedQuantity,
                actual = totalPrice,
                message = "Item total price mismatch"
            )
        }
    }
}