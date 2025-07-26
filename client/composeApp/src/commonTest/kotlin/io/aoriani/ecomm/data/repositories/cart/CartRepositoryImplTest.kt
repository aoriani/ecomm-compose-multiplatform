package io.aoriani.ecomm.data.repositories.cart

import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.model.ZERO
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CartRepositoryImplTest {

    private val productA = ProductPreview(
        id = "prod_A",
        name = "Product A",
        price = DollarAmount("10.00"),
        thumbnailUrl = null
    )
    private val productB = ProductPreview(
        id = "prod_B",
        name = "Product B",
        price = DollarAmount("20.00"),
        thumbnailUrl = null
    )
    private val productC = ProductPreview(
        id = "prod_C",
        name = "Product C",
        price = DollarAmount("5.50"),
        thumbnailUrl = null
    )

    @Test
    fun `Given repository is initialized then initial state is empty and zeroed`() {
        val repository = CartRepositoryImpl()
        val currentState = repository.state.value

        assertTrue(currentState.items.isEmpty(), "Initial items should be empty")
        assertEquals(DollarAmount.ZERO, currentState.subTotal, "Initial subTotal should be zero")
        assertEquals(0, currentState.count, "Initial count should be zero")
    }

    @Test
    fun `When new product is added then state reflects the new item`() {
        val repository = CartRepositoryImpl()
        repository.add(productA)
        val currentState = repository.state.value

        assertEquals(1, currentState.items.size, "Items should contain 1 product")
        assertEquals(productA.toCartItem(), currentState.items[0], "Item should be productA")
        assertEquals(productA.price, currentState.subTotal, "SubTotal should be productA's price")
        assertEquals(1, currentState.count, "Count should be 1")
    }

    @Test
    fun `When same product is added multiple times then quantity and state update correctly`() {
        val repository = CartRepositoryImpl()
        repository.add(productA) // Add first time
        repository.add(productA) // Add second time
        val currentState = repository.state.value

        assertEquals(1, currentState.items.size, "Items should contain 1 product type")
        val expectedCartItem = productA.toCartItem(quantity = 2)
        assertEquals(
            expectedCartItem,
            currentState.items[0],
            "Item should be productA with quantity 2"
        )
        assertEquals(
            productA.price * 2,
            currentState.subTotal,
            "SubTotal should be twice productA's price"
        )
        assertEquals(2, currentState.count, "Count should be 2")
    }

    @Test
    fun `When different products are added then state reflects all items`() {
        val repository = CartRepositoryImpl()
        repository.add(productA)
        repository.add(productB)
        val currentState = repository.state.value

        assertEquals(2, currentState.items.size, "Items should contain 2 products")
        assertTrue(
            currentState.items.contains(productA.toCartItem()),
            "Items should contain productA"
        )
        assertTrue(
            currentState.items.contains(productB.toCartItem()),
            "Items should contain productB"
        )
        assertEquals(
            productA.price + productB.price,
            currentState.subTotal,
            "SubTotal should be sum of prices"
        )
        assertEquals(2, currentState.count, "Count should be 2")
    }

    @Test
    fun `When product quantity is updated then state reflects new quantity and total`() {
        val repository = CartRepositoryImpl()
        repository.add(productA) // Initial quantity 1
        repository.update(productA.id, 3)
        val currentState = repository.state.value

        assertEquals(1, currentState.items.size, "Items should still contain 1 product type")
        val expectedCartItem = productA.toCartItem(quantity = 3)
        assertEquals(
            expectedCartItem,
            currentState.items[0],
            "Item should be productA with quantity 3"
        )
        assertEquals(
            productA.price * 3,
            currentState.subTotal,
            "SubTotal should reflect new quantity"
        )
        assertEquals(3, currentState.count, "Count should be 3")
    }

    @Test
    fun `When product quantity is updated to zero then product is removed and state updates`() {
        val repository = CartRepositoryImpl()
        repository.add(productA)
        repository.add(productB)
        repository.update(productA.id, 0)
        val currentState = repository.state.value

        assertEquals(1, currentState.items.size, "Items should contain 1 product (productB)")
        assertEquals(
            productB.toCartItem(),
            currentState.items[0],
            "Remaining item should be productB"
        )
        assertEquals(productB.price, currentState.subTotal, "SubTotal should be productB's price")
        assertEquals(1, currentState.count, "Count should be 1")
    }

    @Test
    fun `When non-existent product quantity is updated then state remains unchanged`() {
        val repository = CartRepositoryImpl()
        repository.add(productA)
        val initialState = repository.state.value
        repository.update(productB.id, 2) // productB not in cart
        val currentState = repository.state.value

        assertEquals(
            initialState,
            currentState,
            "State should not change when updating non-existent product"
        )
    }

    @Test
    fun `When product is removed then state reflects removal`() {
        val repository = CartRepositoryImpl()
        repository.add(productA)
        repository.add(productB)
        repository.remove(productA.id)
        val currentState = repository.state.value

        assertEquals(1, currentState.items.size, "Items should contain 1 product (productB)")
        assertEquals(
            productB.toCartItem(),
            currentState.items[0],
            "Remaining item should be productB"
        )
        assertEquals(productB.price, currentState.subTotal, "SubTotal should be productB's price")
        assertEquals(1, currentState.count, "Count should be 1")
    }

    @Test
    fun `When non-existent product is removed then state remains unchanged`() {
        val repository = CartRepositoryImpl()
        repository.add(productA)
        val initialState = repository.state.value
        repository.remove(productB.id) // productB not in cart
        val currentState = repository.state.value

        assertEquals(
            initialState,
            currentState,
            "State should not change when removing non-existent product"
        )
    }

    @Test
    fun `When cart is cleared with items then state becomes empty and zeroed`() {
        val repository = CartRepositoryImpl()
        repository.add(productA)
        repository.add(productB)
        repository.clear()
        val currentState = repository.state.value

        assertTrue(currentState.items.isEmpty(), "Items should be empty after clear")
        assertEquals(
            DollarAmount.ZERO,
            currentState.subTotal,
            "SubTotal should be zero after clear"
        )
        assertEquals(0, currentState.count, "Count should be zero after clear")
    }

    @Test
    fun `When cart is cleared while empty then state remains empty and zeroed`() {
        val repository = CartRepositoryImpl()
        repository.clear() // Clear an already empty cart
        val currentState = repository.state.value

        assertTrue(currentState.items.isEmpty(), "Items should remain empty")
        assertEquals(DollarAmount.ZERO, currentState.subTotal, "SubTotal should remain zero")
        assertEquals(0, currentState.count, "Count should remain zero")
    }

    @Test
    fun `Given complex sequence of operations then state is consistently updated`() {
        val repository = CartRepositoryImpl()

        // 1. Add product A
        repository.add(productA) // A($10) qty 1. State: count=1, subTotal=$10
        var state = repository.state.value
        assertEquals(1, state.count)
        assertEquals(DollarAmount("10.00"), state.subTotal)
        assertEquals(persistentListOf(productA.toCartItem(1)), state.items)

        // 2. Add product B
        repository.add(productB) // B($20) qty 1. State: count=2, subTotal=$30 (A:1, B:1)
        state = repository.state.value
        assertEquals(2, state.count)
        assertEquals(DollarAmount("30.00"), state.subTotal)
        assertEquals(persistentListOf(productA.toCartItem(1), productB.toCartItem(1)), state.items)


        // 3. Add product A again (increment quantity)
        repository.add(productA) // A($10) qty 2. State: count=3, subTotal=$40 (A:2, B:1)
        state = repository.state.value
        assertEquals(3, state.count)
        assertEquals(DollarAmount("40.00"), state.subTotal)
        // Order matters for ObservableLinkedHashMap items list
        assertEquals(persistentListOf(productA.toCartItem(2), productB.toCartItem(1)), state.items)


        // 4. Update product B quantity
        repository.update(productB.id, 3) // B($20) qty 3. State: count=5, subTotal=$80 (A:2, B:3)
        state = repository.state.value
        assertEquals(5, state.count)
        assertEquals(DollarAmount("80.00"), state.subTotal)
        assertEquals(persistentListOf(productA.toCartItem(2), productB.toCartItem(3)), state.items)

        // 5. Remove product A
        repository.remove(productA.id) // State: count=3, subTotal=$60 (B:3)
        state = repository.state.value
        assertEquals(3, state.count)
        assertEquals(DollarAmount("60.00"), state.subTotal)
        assertEquals(persistentListOf(productB.toCartItem(3)), state.items)

        // 6. Update product B quantity to 0 (removes it)
        repository.update(productB.id, 0) // State: count=0, subTotal=$0
        state = repository.state.value
        assertEquals(0, state.count)
        assertEquals(DollarAmount.ZERO, state.subTotal)
        assertTrue(state.items.isEmpty())

        // 7. Add product C
        repository.add(productC) // C($5.50) qty 1. State: count=1, subTotal=$5.50
        state = repository.state.value
        assertEquals(1, state.count)
        assertEquals(DollarAmount("5.50"), state.subTotal)
        assertEquals(persistentListOf(productC.toCartItem(1)), state.items)

        // 8. Clear cart
        repository.clear() // State: count=0, subTotal=$0
        state = repository.state.value
        assertEquals(0, state.count)
        assertEquals(DollarAmount.ZERO, state.subTotal)
        assertTrue(state.items.isEmpty())
    }
}
