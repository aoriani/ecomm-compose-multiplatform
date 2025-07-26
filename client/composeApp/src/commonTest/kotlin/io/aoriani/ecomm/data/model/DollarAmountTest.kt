package io.aoriani.ecomm.data.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class DollarAmountTest {

    @Test
    fun `Constructor with valid value`() {
        // Test with integer value
        val amount1 = DollarAmount("100")
        assertEquals("100.00", amount1.toString())

        // Test with decimal value
        val amount2 = DollarAmount("99.99")
        assertEquals("99.99", amount2.toString())

        // Test with zero
        val amount3 = DollarAmount("0")
        assertEquals("0.00", amount3.toString())
    }

    @Test
    fun `Constructor with negative value`() {
        // Test with negative value
        val amount = DollarAmount("-50.25")
        assertEquals("-50.25", amount.toString())
    }

    @Test
    fun `Plus operation`() {
        // Test adding two positive values
        val amount1 = DollarAmount("10.50")
        val amount2 = DollarAmount("5.25")
        val sum = amount1 + amount2
        assertEquals("15.75", sum.toString())

        // Test adding with zero
        val amount3 = DollarAmount("0")
        val sum2 = amount1 + amount3
        assertEquals("10.50", sum2.toString())

        // Test adding negative value
        val amount4 = DollarAmount("-3.75")
        val sum3 = amount1 + amount4
        assertEquals("6.75", sum3.toString())
    }

    @Test
    fun `Times operation`() {
        // Test multiplying by positive integer
        val amount1 = DollarAmount("10.50")
        val product1 = amount1 * 3
        assertEquals("31.50", product1.toString())

        // Test multiplying by zero
        val product2 = amount1 * 0
        assertEquals("0.00", product2.toString())

        // Test multiplying by negative integer
        val product3 = amount1 * (-2)
        assertEquals("-21.00", product3.toString())

        // Test with a large multiplier
        val amount2 = DollarAmount("0.01")
        val product4 = amount2 * 1000
        assertEquals("10.00", product4.toString())
    }

    @Test
    fun `To string`() {
        // Test formatting with exactly 2 decimal places
        val amount1 = DollarAmount("10.5")
        assertEquals("10.50", amount1.toString())

        val amount2 = DollarAmount("10")
        assertEquals("10.00", amount2.toString())

        val amount3 = DollarAmount("10.00")
        assertEquals("10.00", amount3.toString())

        val amount4 = DollarAmount("10.567")  // Should round to 2 decimal places
        assertEquals("10.57", amount4.toString())  // Assuming rounding is implemented
    }

    @Test
    fun `Serialization and deserialization`() {
        // Create a DollarAmount instance
        val originalAmount = DollarAmount("42.99")

        // Create a Json instance
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        // Serialize to JSON
        val jsonString = json.encodeToString(DollarAmount.serializer(), originalAmount)

        // Verify the serialized format (should be a string representation)
        assertEquals("\"42.99\"", jsonString)

        // Deserialize from JSON
        val deserializedAmount = json.decodeFromString(DollarAmount.serializer(), jsonString)

        // Verify the deserialized value matches the original
        assertEquals(originalAmount.toString(), deserializedAmount.toString())

        // Test with a different value
        val anotherAmount = DollarAmount("99.50")
        val anotherJsonString = json.encodeToString(DollarAmount.serializer(), anotherAmount)
        assertEquals("\"99.50\"", anotherJsonString)

        val anotherDeserialized =
            json.decodeFromString(DollarAmount.serializer(), anotherJsonString)
        assertEquals(anotherAmount.toString(), anotherDeserialized.toString())
    }

    @Test
    fun `ZERO value`() {
        // Test string representation
        assertEquals("0.00", DollarAmount.ZERO.toString())

        // Test addition with ZERO
        val amount = DollarAmount("123.45")
        assertEquals(amount.toString(), (amount + DollarAmount.ZERO).toString())
        assertEquals(amount.toString(), (DollarAmount.ZERO + amount).toString())

        // Test multiplication by ZERO
        assertEquals("0.00", (amount * 0).toString()) // Though this is already covered by `Times operation`
                                                       // it's good to be explicit for ZERO.
        // Test multiplication of ZERO by a number
        assertEquals("0.00", (DollarAmount.ZERO * 10).toString())
    }

    @Test
    fun `Equals method works correctly`() {
        val amount1a = DollarAmount("100.00")
        val amount1b = DollarAmount("100.00")
        val amount1c = DollarAmount("100") // Normalizes to "100.00" via toString()

        val amount2 = DollarAmount("99.99")

        val amount3a = DollarAmount("-50.25")
        val amount3b = DollarAmount("-50.25")

        val amount4a = DollarAmount("0")
        val amount4b = DollarAmount("0.00")
        val amount4c = DollarAmount.ZERO

        // Test case 1: Two DollarAmount objects with the same explicit string value
        assertTrue(amount1a == amount1b, "amount1a (100.00) should be equal to amount1b (100.00)")
        assertTrue(amount1b == amount1a, "Symmetry: amount1b (100.00) should be equal to amount1a (100.00)")
        assertEquals(amount1a, amount1b, "amount1a (100.00) should be equal to amount1b (100.00) (assertEquals)")

        // Test case 2: Two DollarAmount objects with different initial strings but same normalized value
        assertTrue(amount1a == amount1c, "amount1a (100.00) should be equal to amount1c (100)")
        assertTrue(amount1c == amount1a, "Symmetry: amount1c (100) should be equal to amount1a (100.00)")
        assertEquals(amount1a, amount1c, "amount1a (100.00) should be equal to amount1c (100) (assertEquals)")

        // Test case 3: Two DollarAmount objects with different values
        assertFalse(amount1a == amount2, "amount1a (100.00) should not be equal to amount2 (99.99)")
        assertNotEquals(amount1a, amount2, "amount1a (100.00) should not be equal to amount2 (99.99) (assertNotEquals)")

        // Test case 4: Equality with self (reflexivity)
        assertTrue(amount1a == amount1a, "amount1a (100.00) should be equal to itself")

        // Test case 5: Inequality with null
        assertFalse(amount1a.equals(null), "amount1a (100.00) should not be equal to null")

        // Test case 6: Inequality with a different type
        assertFalse(amount1a.equals("100.00"), "amount1a (100.00) should not be equal to a String \"100.00\"")

        // Test case 7: Negative values
        assertTrue(amount3a.equals(amount3b), "amount3a (-50.25) should be equal to amount3b (-50.25)")
        assertEquals(amount3a, amount3b, "amount3a (-50.25) should be equal to amount3b (-50.25) (assertEquals)")
        assertFalse(amount1a.equals(amount3a), "amount1a (100.00) should not be equal to amount3a (-50.25)")

        // Test case 8: Zero values - comparing different initial representations of zero
        assertTrue(amount4a.equals(amount4b), "amount4a (0) should be equal to amount4b (0.00)")
        assertEquals(amount4a, amount4b, "amount4a (0) should be equal to amount4b (0.00) (assertEquals)")
        assertTrue(amount4a.equals(amount4c), "amount4a (0) should be equal to amount4c (DollarAmount.ZERO)")
        assertEquals(amount4a, amount4c, "amount4a (0) should be equal to amount4c (DollarAmount.ZERO) (assertEquals)")
        assertTrue(DollarAmount.ZERO.equals(DollarAmount("0.00")), "DollarAmount.ZERO should be equal to DollarAmount(\"0.00\")")

        // Test case 9: Transitivity (if a=b and b=c, then a=c)
        val valA = DollarAmount("50.00")
        val valB = DollarAmount("50.00")
        val valC = DollarAmount("50") // Normalizes to "50.00"
        assertTrue(valA.equals(valB), "Transitivity: valA (50.00) should be equal to valB (50.00)")
        assertTrue(valB.equals(valC), "Transitivity: valB (50.00) should be equal to valC (50)")
        assertTrue(valA.equals(valC), "Transitivity: valA (50.00) should be equal to valC (50)")

        // Test case 10: Consistency (multiple invocations of equals yield the same result)
        assertTrue(amount1a.equals(amount1b), "Consistency: amount1a.equals(amount1b) first call")
        assertTrue(amount1a.equals(amount1b), "Consistency: amount1a.equals(amount1b) second call")
        assertFalse(amount1a.equals(amount2), "Consistency: amount1a.equals(amount2) first call")
        assertFalse(amount1a.equals(amount2), "Consistency: amount1a.equals(amount2) second call")

        // Test case 11: Normalization cases from `To string` test
        val toStringTest1 = DollarAmount("10.5") // Normalizes to "10.50"
        val toStringTest2 = DollarAmount("10.50")
        assertTrue(toStringTest1.equals(toStringTest2), "DollarAmount('10.5') should equal DollarAmount('10.50')")
        assertEquals(toStringTest1, toStringTest2, "DollarAmount('10.5') vs DollarAmount('10.50') (assertEquals)")

        val toStringTest3 = DollarAmount("10") // Normalizes to "10.00"
        val toStringTest4 = DollarAmount("10.00")
        assertTrue(toStringTest3.equals(toStringTest4), "DollarAmount('10') should equal DollarAmount('10.00')")
        assertEquals(toStringTest3, toStringTest4, "DollarAmount('10') vs DollarAmount('10.00') (assertEquals)")

        // Test case 12: Rounding cases from `To string` test
//        val rounding1 = DollarAmount("10.567") // Normalizes to "10.57"
//        val rounding2 = DollarAmount("10.57")
//        assertTrue(rounding1.equals(rounding2), "DollarAmount('10.567') should equal DollarAmount('10.57')")
//        assertEquals(rounding1, rounding2, "DollarAmount('10.567') vs DollarAmount('10.57') (assertEquals)")

        // Test case 13: Values with excessive or missing trailing zeros, assuming normalization
        val amountWithTrailingZeros = DollarAmount("75.50000") // Should normalize to "75.50"
        val amountStandard = DollarAmount("75.50")
        val amountNoTrailingDecimal = DollarAmount("75.5")      // Should normalize to "75.50"

        assertTrue(amountStandard.equals(amountWithTrailingZeros), "DollarAmount('75.50') should equal DollarAmount('75.50000')")
        assertEquals(amountStandard, amountWithTrailingZeros)

        assertTrue(amountStandard.equals(amountNoTrailingDecimal), "DollarAmount('75.50') should equal DollarAmount('75.5')")
        assertEquals(amountStandard, amountNoTrailingDecimal)

        assertTrue(amountWithTrailingZeros.equals(amountNoTrailingDecimal), "DollarAmount('75.50000') should equal DollarAmount('75.5')")
        assertEquals(amountWithTrailingZeros, amountNoTrailingDecimal)
    }
}
