package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.BlankProductIdException
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HardcodedProductRepositoryImplTest {


    @Test
    fun `When getById is called with an empty ID then it throws BlankProductIdException`() {
        // Then
        assertFailsWith<BlankProductIdException> {
            runTest { 
                // This will throw directly from the ProductId constructor
                HardcodedProductRepositoryImpl.getById(ProductId(""))
            }
        }
    }
    
    @Test
    fun `When getAll is called then it returns 16 products`() = runTest {
        val result = HardcodedProductRepositoryImpl.getAll()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(16, result.getOrThrow().size)
    }
    
    @Test
    fun `When getById is called with an actual product ID then it returns that product`() = runTest {
        // When
        val result = HardcodedProductRepositoryImpl.getById(ProductId("elon_musk_plush"))
        
        // Then
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("Elon Musk", result.getOrNull()?.name)
        assertEquals(BigDecimal("34.99"), result.getOrNull()?.price)
    }
    
    @Test
    fun `When getById is called with an actual nonexistent ID then it returns a failure`() = runTest {
        // When
        val result = HardcodedProductRepositoryImpl.getById(ProductId("nonexistent_plush"))
        
        // Then
        assertTrue(result.isFailure)
        assertIs<ProductNotFoundException>(result.exceptionOrNull())
        assertEquals("Product with id 'nonexistent_plush' not found.", result.exceptionOrNull()?.message)
    }
}