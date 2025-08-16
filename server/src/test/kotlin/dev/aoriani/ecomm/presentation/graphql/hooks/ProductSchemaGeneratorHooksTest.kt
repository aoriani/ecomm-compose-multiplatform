package dev.aoriani.ecomm.presentation.graphql.hooks

import com.expediagroup.graphql.generator.scalars.ID
import graphql.Scalars
import graphql.scalars.ExtendedScalars
import java.math.BigDecimal
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductSchemaGeneratorHooksTest {

    @Test
    fun `When willGenerateGraphQLType is called with BigDecimal then it returns GraphQLBigDecimal`() {
        // When
        val result = ProductSchemaGeneratorHooks.willGenerateGraphQLType(typeOf<BigDecimal>())
        
        // Then
        assertEquals(ExtendedScalars.GraphQLBigDecimal, result)
    }
    
    @Test
    fun `When willGenerateGraphQLType is called with Number then it returns GraphQLBigDecimal`() {
        // When
        val result = ProductSchemaGeneratorHooks.willGenerateGraphQLType(typeOf<Number>())
        
        // Then
        assertEquals(ExtendedScalars.GraphQLBigDecimal, result)
    }
    
    @Test
    fun `When willGenerateGraphQLType is called with ID then it returns GraphQLID`() {
        // When
        val result = ProductSchemaGeneratorHooks.willGenerateGraphQLType(typeOf<ID>())
        
        // Then
        assertEquals(Scalars.GraphQLID, result)
    }
    
    @Test
    fun `When willGenerateGraphQLType is called with String then it returns null`() {
        // When
        val result = ProductSchemaGeneratorHooks.willGenerateGraphQLType(typeOf<String>())
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `When willGenerateGraphQLType is called with Boolean then it returns null`() {
        // When
        val result = ProductSchemaGeneratorHooks.willGenerateGraphQLType(typeOf<Boolean>())
        
        // Then
        assertNull(result)
    }
}