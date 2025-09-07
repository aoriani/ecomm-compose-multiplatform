package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DollarAmountAsStringSerializerTest {

    private val serializer = DollarAmountAsStringSerializer
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `descriptor has correct properties`() {
        // Verify the descriptor properties
        assertEquals("io.aoriani.ecomm.data.model.DollarAmount", serializer.descriptor.serialName)
        assertEquals(PrimitiveKind.STRING, serializer.descriptor.kind)
    }

    @Test
    fun `serialize basic positive amounts`() {
        val testCases = mapOf(
            DollarAmount("10.50") to "\"10.50\"",
            DollarAmount("100.00") to "\"100.00\"",
            DollarAmount("0.01") to "\"0.01\"",
            DollarAmount("999.99") to "\"999.99\"",
            DollarAmount("1234.56") to "\"1234.56\""
        )

        testCases.forEach { (amount, expectedJson) ->
            val result = json.encodeToString(serializer, amount)
            assertEquals(expectedJson, result, "Failed for amount: ${amount.toString()}")
        }
    }

    @Test
    fun `serialize negative amounts`() {
        val testCases = mapOf(
            DollarAmount("-10.50") to "\"-10.50\"",
            DollarAmount("-100.00") to "\"-100.00\"",
            DollarAmount("-0.01") to "\"-0.01\"",
            DollarAmount("-999.99") to "\"-999.99\"",
            DollarAmount("-1234.56") to "\"-1234.56\""
        )

        testCases.forEach { (amount, expectedJson) ->
            val result = json.encodeToString(serializer, amount)
            assertEquals(expectedJson, result, "Failed for negative amount: ${amount.toString()}")
        }
    }

    @Test
    fun `serialize zero amounts`() {
        val testCases = mapOf(
            DollarAmount("0") to "\"0.00\"",
            DollarAmount("0.0") to "\"0.00\"",
            DollarAmount("0.00") to "\"0.00\"",
            DollarAmount.ZERO to "\"0.00\""
        )

        testCases.forEach { (amount, expectedJson) ->
            val result = json.encodeToString(serializer, amount)
            assertEquals(expectedJson, result, "Failed for zero amount: ${amount.toString()}")
        }
    }

    @Test
    fun `serialize large amounts`() {
        val testCases = mapOf(
            DollarAmount("999999.99") to "\"999999.99\"",
            DollarAmount("1000000.00") to "\"1000000.00\"",
            DollarAmount("123456789.12") to "\"123456789.12\"",
            DollarAmount("-999999.99") to "\"-999999.99\""
        )

        testCases.forEach { (amount, expectedJson) ->
            val result = json.encodeToString(serializer, amount)
            assertEquals(expectedJson, result, "Failed for large amount: ${amount.toString()}")
        }
    }

    @Test
    fun `serialize amounts with rounding`() {
        // Test values that need rounding to 2 decimal places
        val testCases = mapOf(
            DollarAmount("10.567") to "\"10.57\"",  // Rounds up
            DollarAmount("10.564") to "\"10.56\"",  // Rounds down
            DollarAmount("10.565") to "\"10.56\"",  // Half-even rounding to even digit (6)
            DollarAmount("10.575") to "\"10.58\"",  // Half-even rounding to even digit (8)
            DollarAmount("123.456789") to "\"123.46\"" // Multiple decimals
        )

        testCases.forEach { (amount, expectedJson) ->
            val result = json.encodeToString(serializer, amount)
            assertEquals(expectedJson, result, "Failed for rounding amount: ${amount.toString()}")
        }
    }

    @Test
    fun `deserialize valid string amounts`() {
        val testCases = mapOf(
            "\"10.50\"" to "10.50",
            "\"100.00\"" to "100.00",
            "\"0.01\"" to "0.01",
            "\"999.99\"" to "999.99",
            "\"1234.56\"" to "1234.56",
            "\"0\"" to "0.00",
            "\"0.0\"" to "0.00",
            "\"10\"" to "10.00"
        )

        testCases.forEach { (jsonString, expectedAmount) ->
            val result = json.decodeFromString(serializer, jsonString)
            assertEquals(expectedAmount, result.toString(), "Failed deserializing: $jsonString")
        }
    }

    @Test
    fun `deserialize negative amounts`() {
        val testCases = mapOf(
            "\"-10.50\"" to "-10.50",
            "\"-100.00\"" to "-100.00",
            "\"-0.01\"" to "-0.01",
            "\"-999.99\"" to "-999.99",
            "\"-0\"" to "0.00"
        )

        testCases.forEach { (jsonString, expectedAmount) ->
            val result = json.decodeFromString(serializer, jsonString)
            assertEquals(expectedAmount, result.toString(), "Failed deserializing negative: $jsonString")
        }
    }

    @Test
    fun `deserialize large amounts`() {
        val testCases = mapOf(
            "\"999999.99\"" to "999999.99",
            "\"1000000.00\"" to "1000000.00",
            "\"123456789.12\"" to "123456789.12",
            "\"-999999.99\"" to "-999999.99"
        )

        testCases.forEach { (jsonString, expectedAmount) ->
            val result = json.decodeFromString(serializer, jsonString)
            assertEquals(expectedAmount, result.toString(), "Failed deserializing large: $jsonString")
        }
    }

    @Test
    fun `deserialize invalid format strings throws exception`() {
        val invalidCases = listOf(
            "\"10.\"",       // Ends with decimal point
            "\".50\"",       // Starts with decimal point
            "\"abc\"",       // Non-numeric
            "\"10.50.25\"",  // Multiple decimal points
            "\"--10\"",      // Double minus
            "\"10-\"",       // Minus at end
            "\"\"",          // Empty string
            "\" 10\"",       // Leading space
            "\"10 \"",       // Trailing space
            "\"+10\"",       // Plus sign
            "\"$10\"",       // Dollar sign
            "\"10,50\"",     // Comma separator
            "\".\"",         // Just decimal point
            "\"-\""          // Just minus sign
        )

        invalidCases.forEach { invalidJson ->
            assertFailsWith<DollarAmountFormatException>(
                message = "Should throw DollarAmountFormatException for: $invalidJson"
            ) {
                json.decodeFromString(serializer, invalidJson)
            }
        }
    }

    @Test
    fun `round-trip serialization preserves values`() {
        val testAmounts = listOf(
//            DollarAmount("10.50"),
//            DollarAmount("0"),
//            DollarAmount("-25.75"),
//            DollarAmount("999999.99"),
//            DollarAmount("0.01"),
//            DollarAmount.ZERO,
//            DollarAmount("123.456"), // Will be normalized to 123.46
            DollarAmount("-0.001")   // Will be normalized
        )

        testAmounts.forEach { originalAmount ->
            // Serialize
            val serialized = json.encodeToString(serializer, originalAmount)
            
            // Deserialize
            val deserialized = json.decodeFromString(serializer, serialized)
            
            // Compare using normalized string representation
            assertEquals(
                originalAmount.toString(), 
                deserialized.toString(),
                "Round-trip failed for: $originalAmount"
            )
            
            // Verify equality
            assertEquals(originalAmount, deserialized, "Objects should be equal after round-trip")
        }
    }

    @Test
    fun `serialize with different JSON configurations`() {
        val amount = DollarAmount("42.99")
        
        // Test with strict JSON
        val strictJson = Json { 
            isLenient = false
            ignoreUnknownKeys = false
        }
        val strictResult = strictJson.encodeToString(serializer, amount)
        assertEquals("\"42.99\"", strictResult)
        
        // Test with lenient JSON
        val lenientJson = Json { 
            isLenient = true 
            ignoreUnknownKeys = true
        }
        val lenientResult = lenientJson.encodeToString(serializer, amount)
        assertEquals("\"42.99\"", lenientResult)
        
        // Both should deserialize to the same result
        val strictDeserialized = strictJson.decodeFromString(serializer, strictResult)
        val lenientDeserialized = lenientJson.decodeFromString(serializer, lenientResult)
        assertEquals(strictDeserialized, lenientDeserialized)
    }

    @Test
    fun `serialize in complex JSON objects`() {
        // Test serialization as part of a larger JSON structure
        @Serializable
        data class Product(val name: String, val price: DollarAmount)
        
        val product = Product("Test Item", DollarAmount("19.99"))
        val productJson = Json.encodeToString(product)
        
        // Verify the price is serialized as a string
        assertTrue(productJson.contains("\"price\":\"19.99\""))
        
        // Verify round-trip
        val deserializedProduct = Json.decodeFromString<Product>(productJson)
        assertEquals(product.price.toString(), deserializedProduct.price.toString())
    }

    @Test
    fun `deserialize with precision edge cases`() {
        // Test cases that might have precision issues with floating point
        val precisionCases = mapOf(
            "\"0.10\"" to "0.10",
            "\"0.20\"" to "0.20",
            "\"0.30\"" to "0.30",  // Common floating point precision issue
            "\"1.10\"" to "1.10",
            "\"2.20\"" to "2.20",
            "\"3.30\"" to "3.30"
        )
        
        precisionCases.forEach { (jsonString, expectedAmount) ->
            val result = json.decodeFromString(serializer, jsonString)
            assertEquals(expectedAmount, result.toString(), "Precision issue for: $jsonString")
        }
    }

    @Test
    fun `serialize handles toString normalization`() {
        // Test that serialization uses the normalized toString() representation
        val testCases = listOf(
            // Input format -> Expected serialized format
            "10" to "\"10.00\"",     // No decimals -> 2 decimals
            "10.5" to "\"10.50\"",   // 1 decimal -> 2 decimals
            "10.500" to "\"10.50\"", // Extra zeros removed
            "10.567" to "\"10.57\"", // Rounded to 2 decimals
            "0" to "\"0.00\"",       // Zero normalized
            "-0" to "\"0.00\""      // Negative zero
        )
        
        testCases.forEach { (input, expectedSerialized) ->
            val amount = DollarAmount(input)
            val serialized = json.encodeToString(serializer, amount)
            assertEquals(expectedSerialized, serialized, "Normalization failed for input: $input")
        }
    }

    @Test
    fun `verify serializer consistency with DollarAmount operations`() {
        // Test that serializer works correctly with DollarAmount arithmetic results
        val amount1 = DollarAmount("10.50")
        val amount2 = DollarAmount("5.25")
        
        // Test addition result serialization
        val sum = amount1 + amount2
        val sumSerialized = json.encodeToString(serializer, sum)
        assertEquals("\"15.75\"", sumSerialized)
        
        // Test multiplication result serialization
        val product = amount1 * 3
        val productSerialized = json.encodeToString(serializer, product)
        assertEquals("\"31.50\"", productSerialized)
        
        // Test zero result
        val zero = amount1 * 0
        val zeroSerialized = json.encodeToString(serializer, zero)
        assertEquals("\"0.00\"", zeroSerialized)
    }
}