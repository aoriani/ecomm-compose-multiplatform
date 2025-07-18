package dev.aoriani.ecomm.repository

import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HardcodedProductRepositoryImplTest {

    @Test
    fun `When getAll is called then all products are returned`() = runBlocking {
        // When
        val products = HardcodedProductRepositoryImpl.getAll()

        // Then
        assertEquals(16, products.size)
    }

    @Test
    fun `When getById is called with a valid ID then the correct product is returned`() = runBlocking {
        // When
        val product = HardcodedProductRepositoryImpl.getById("elon_musk_plush")

        // Then
        assertNotNull(product)
        assertEquals("Elon Musk", product.name)
    }

    @Test
    fun `When getById is called with a nonexistent ID then null is returned`() = runBlocking {
        // When
        val product = HardcodedProductRepositoryImpl.getById("nonexistent_id")

        // Then
        assertNull(product)
    }

    @Test
    fun `When getById is called with an empty ID then null is returned`() = runBlocking {
        // When
        val product = HardcodedProductRepositoryImpl.getById("")

        // Then
        assertNull(product)
    }
}