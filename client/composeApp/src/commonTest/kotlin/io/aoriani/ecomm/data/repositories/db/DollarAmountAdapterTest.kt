package io.aoriani.ecomm.data.repositories.db

import io.aoriani.ecomm.data.model.DollarAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class DollarAmountAdapterTest {

    private val adapter = DollarAmountAdapter()

    @Test
    fun `When decode is called with valid dollar string then returns correct DollarAmount`() {
        // Arrange
        val databaseValue = "10.99"
        
        // Act
        val result = adapter.decode(databaseValue)
        
        // Assert
        assertNotNull(result)
        assertEquals("10.99", result.toString())
    }

    @Test
    fun `When decode is called with zero string then returns correct DollarAmount`() {
        // Arrange
        val databaseValue = "0"
        
        // Act
        val result = adapter.decode(databaseValue)
        
        // Assert
        assertNotNull(result)
        assertEquals("0.00", result.toString())
    }

    @Test
    fun `When decode is called with empty string then throws exception`() {
        // Arrange
        val databaseValue = ""
        
        // Act & Assert
        assertFailsWith<io.aoriani.ecomm.data.model.DollarAmountFormatException> {
            adapter.decode(databaseValue)
        }
    }

    @Test
    fun `When decode is called with large dollar amount then returns correct DollarAmount`() {
        // Arrange
        val databaseValue = "999999.99"
        
        // Act
        val result = adapter.decode(databaseValue)
        
        // Assert
        assertNotNull(result)
        assertEquals("999999.99", result.toString())
    }

    @Test
    fun `When decode is called with decimal format then returns correct DollarAmount`() {
        // Arrange
        val databaseValue = "123.45"
        
        // Act
        val result = adapter.decode(databaseValue)
        
        // Assert
        assertNotNull(result)
        assertEquals("123.45", result.toString())
    }

    @Test
    fun `When encode is called with valid DollarAmount then returns correct string`() {
        // Arrange
        val dollarAmount = DollarAmount("25.50")
        
        // Act
        val result = adapter.encode(dollarAmount)
        
        // Assert
        assertEquals("25.50", result)
    }

    @Test
    fun `When encode is called with zero DollarAmount then returns zero string`() {
        // Arrange
        val dollarAmount = DollarAmount("0")
        
        // Act
        val result = adapter.encode(dollarAmount)
        
        // Assert
        assertEquals("0.00", result)
    }


    @Test
    fun `When encode is called with large DollarAmount then returns correct string`() {
        // Arrange
        val dollarAmount = DollarAmount("1000000.00")
        
        // Act
        val result = adapter.encode(dollarAmount)
        
        // Assert
        assertEquals("1000000.00", result)
    }

    @Test
    fun `Given round-trip conversion When decode then encode then result matches original string`() {
        // Arrange
        val originalValue = "42.75"
        
        // Act
        val decoded = adapter.decode(originalValue)
        val encoded = adapter.encode(decoded)
        
        // Assert
        assertEquals(originalValue, encoded)
    }

    @Test
    fun `Given round-trip conversion When encode then decode then result equals original DollarAmount`() {
        // Arrange
        val originalAmount = DollarAmount("99.99")
        
        // Act
        val encoded = adapter.encode(originalAmount)
        val decoded = adapter.decode(encoded)
        
        // Assert
        assertEquals(originalAmount.toString(), decoded.toString())
    }

    @Test
    fun `Given multiple round-trip conversions When performed consecutively then values remain consistent`() {
        // Arrange
        val testValues = listOf("0.00", "1.00", "10.50", "999.99", "12345.67")
        
        // Act & Assert
        testValues.forEach { originalValue ->
            val decoded = adapter.decode(originalValue)
            val encoded = adapter.encode(decoded)
            val decodedAgain = adapter.decode(encoded)
            
            assertEquals(encoded, decodedAgain.toString(), "Round-trip failed for $originalValue")
        }
    }

    @Test
    fun `When decode is called with various string formats then creates valid DollarAmount objects`() {
        // Arrange
        val testCases = mapOf(
            "5" to "5.00",
            "5.0" to "5.00",
            "5.00" to "5.00",
            "0.01" to "0.01",
            "0.10" to "0.10",
            "100" to "100.00",
            "1000.5" to "1000.50"
        )
        
        // Act & Assert
        testCases.forEach { (input, expectedOutput) ->
            val result = adapter.decode(input)
            assertEquals(expectedOutput, result.toString(), "Failed for input: $input")
        }
    }

    @Test
    fun `When encode is called with various DollarAmount objects then returns correct strings`() {
        // Arrange
        val testCases = mapOf(
            "0" to "0.00",
            "1" to "1.00",
            "10.5" to "10.50",
            "100.00" to "100.00",
            "999.99" to "999.99",
            "5000" to "5000.00"
        )
        
        // Act & Assert
        testCases.forEach { (input, expectedOutput) ->
            val dollarAmount = DollarAmount(input)
            val result = adapter.encode(dollarAmount)
            assertEquals(expectedOutput, result, "Failed for DollarAmount with value: $input")
        }
    }
}