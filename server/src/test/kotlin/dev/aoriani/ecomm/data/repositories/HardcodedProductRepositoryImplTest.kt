package dev.aoriani.ecomm.data.repositories

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HardcodedProductRepositoryImplTest {

    @Test
    fun `When getAll is called then all products are returned`() = runBlocking {
        // When
        val result = HardcodedProductRepositoryImpl.getAll()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(16, result.getOrThrow().size)
    }

    @Test
    fun `When getById is called with a valid ID then the correct product is returned`() = runBlocking {
        // When
        val resul = HardcodedProductRepositoryImpl.getById("elon_musk_plush")

        // Then
        assertTrue(resul.isSuccess)
        assertNotNull(resul.getOrNull())
        assertEquals("Elon Musk", resul.getOrNull()?.name)
    }

    @Test
    fun `When getById is called with a nonexistent ID then null is returned`() = runBlocking {
        // When
        val result = HardcodedProductRepositoryImpl.getById("nonexistent_id")

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `When getById is called with an empty ID then null is returned`() = runBlocking {
        // When
        val result = HardcodedProductRepositoryImpl.getById("")

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }
}