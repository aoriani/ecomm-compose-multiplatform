package dev.aoriani.ecomm.presentation.graphql.exceptions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GraphQLInternalExceptionTest {

    @Test
    fun `When created with default parameters then it has default message and null cause`() {
        // When
        val exception = GraphQLInternalException()
        
        // Then
        assertEquals("An internal error occurred", exception.message)
        assertNull(exception.cause)
    }
    
    @Test
    fun `When created with custom message then it has that message`() {
        // Given
        val customMessage = "Custom error message"
        
        // When
        val exception = GraphQLInternalException(customMessage)
        
        // Then
        assertEquals(customMessage, exception.message)
        assertNull(exception.cause)
    }
    
    @Test
    fun `When created with cause then it has that cause`() {
        // Given
        val cause = RuntimeException("Original error")
        
        // When
        val exception = GraphQLInternalException(cause = cause)
        
        // Then
        assertEquals("An internal error occurred", exception.message)
        assertEquals(cause, exception.cause)
    }
    
    @Test
    fun `When created with custom message and cause then it has both`() {
        // Given
        val customMessage = "Custom error message"
        val cause = RuntimeException("Original error")
        
        // When
        val exception = GraphQLInternalException(customMessage, cause)
        
        // Then
        assertEquals(customMessage, exception.message)
        assertEquals(cause, exception.cause)
    }
}