package dev.aoriani.ecomm.presentation.mcp.models

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalPriceSerializerTest {

    @Test
    fun `When serializing BigDecimal then it outputs double with 2 decimal places`() {
        // Given
        val price = BigDecimal("19.99")
        
        // When
        val serialized = Json.encodeToString(BigDecimalPriceSerializer, price)
        
        // Then
        assertEquals("19.99", serialized)
    }
    
    @Test
    fun `When serializing BigDecimal with more than 2 decimal places then it rounds to 2 places`() {
        // Given
        val price = BigDecimal("19.999")
        
        // When
        val serialized = Json.encodeToString(BigDecimalPriceSerializer, price)
        
        // Then
        assertEquals("20.0", serialized)
    }
    
    @Test
    fun `When serializing BigDecimal with no decimal places then it adds zeroes`() {
        // Given
        val price = BigDecimal("20")
        
        // When
        val serialized = Json.encodeToString(BigDecimalPriceSerializer, price)
        
        // Then
        assertEquals("20.0", serialized)
    }
    
    @Test
    fun `When deserializing string then it creates correct BigDecimal`() {
        // Given
        val priceString = "\"19.99\""
        
        // When
        val deserialized = Json.decodeFromString(BigDecimalPriceSerializer, priceString)
        
        // Then
        assertEquals(BigDecimal("19.99"), deserialized)
    }
    
    @Test
    fun `When deserializing integer string then it creates correct BigDecimal`() {
        // Given
        val priceString = "\"20\""
        
        // When
        val deserialized = Json.decodeFromString(BigDecimalPriceSerializer, priceString)
        
        // Then
        assertEquals(BigDecimal("20"), deserialized)
    }
    
    @Test
    fun `When serializing and then deserializing BigDecimal then the value is preserved`() {
        // Given
        val originalPrice = BigDecimal("19.99")
        
        // When - use a custom Json configuration that will encode the double as a string
        val json = Json { 
            encodeDefaults = true
            isLenient = true
        }
        val serialized = json.encodeToString(BigDecimalPriceSerializer, originalPrice)
        val deserialized = json.decodeFromString(BigDecimalPriceSerializer, serialized)
        
        // Then
        assertEquals(originalPrice, deserialized)
    }
}