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

        assertCartState(
            expectedSize = 1,
            expectedCount = 2,
            expectedSubTotal = DollarAmount("20.00")
        )

        val firstCartItem = cartRepository.state.value.items.first()
        assertCartItem(
            cartItem = firstCartItem,
            expectedProduct = fakeProduct1,
            expectedQuantity = 2
        )
    }

    @Test
    fun `When adding multiple products they are added to the cart`() = runTest {
        cartRepository.add(fakeProduct1)
        cartRepository.add(fakeProduct2)

        assertCartState(
            expectedSize = 2,
            expectedCount = 2,
            expectedSubTotal = DollarAmount("30.00")
        )

        val product1InCart = cartRepository.state.value.items.find { it.id == fakeProduct1.id }
        assertCartItem(
            cartItem = product1InCart,
            expectedProduct = fakeProduct1,
            expectedQuantity = 1
        )

        val product2InCart = cartRepository.state.value.items.find { it.id == fakeProduct2.id }
        assertCartItem(
            cartItem = product2InCart,
            expectedProduct = fakeProduct2,
            expectedQuantity = 1
        )
    }

    @Test
    fun `When updating the quantity of a product it is updated in the cart`() = runTest {
        cartRepository.add(fakeProduct1)

        assertCartState(
            expectedSize = 1,
            expectedCount = 1,
            expectedSubTotal = DollarAmount("10.00")
        )
        assertCartItem(
            cartItem = cartRepository.state.value.items.first(),
            expectedProduct = fakeProduct1,
            expectedQuantity = 1
        )

        cartRepository.updateQuantity(productId = fakeProduct1.id, quantity = 3)

        assertCartState(
            expectedSize = 1,
            expectedCount = 3,
            expectedSubTotal = DollarAmount("30.00")
        )
        assertCartItem(
            cartItem = cartRepository.state.value.items.first(),
            expectedProduct = fakeProduct1,
            expectedQuantity = 3
        )
    }

    @Test
    fun `When removing a product it is removed from the cart`() = runTest {
        cartRepository.add(fakeProduct1)
        cartRepository.add(fakeProduct2)

        assertCartState(
            expectedSize = 2,
            expectedCount = 2,
            expectedSubTotal = DollarAmount("30.00")
        )

        cartRepository.remove(fakeProduct1.id)

        assertCartState(
            expectedSize = 1,
            expectedCount = 1,
            expectedSubTotal = DollarAmount("20.00")
        )

        val firstCartItem = cartRepository.state.value.items.first()
        assertCartItem(
            cartItem = firstCartItem,
            expectedProduct = fakeProduct2, // Product 1 was removed
            expectedQuantity = 1
        )
    }

    @Test
    fun `When clearing the cart it becomes empty`() = runTest {
        cartRepository.add(fakeProduct1)
        cartRepository.add(fakeProduct2)

        assertCartState(
            expectedSize = 2,
            expectedCount = 2,
            expectedSubTotal = DollarAmount("30.00")
        )

        cartRepository.clear()

        assertCartState(
            expectedSize = 0,
            expectedCount = 0,
            expectedSubTotal = DollarAmount.ZERO
        )
        assertTrue(cartRepository.state.value.items.isEmpty(), "Cart items should be empty after clear")
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