package io.aoriani.ecomm.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

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

        val anotherDeserialized = json.decodeFromString(DollarAmount.serializer(), anotherJsonString)
        assertEquals(anotherAmount.toString(), anotherDeserialized.toString())
    }
}
