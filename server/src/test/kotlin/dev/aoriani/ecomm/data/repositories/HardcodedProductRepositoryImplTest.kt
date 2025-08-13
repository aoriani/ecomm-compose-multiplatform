package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.BlankProductIdException
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
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
        val resul = HardcodedProductRepositoryImpl.getById(ProductId("elon_musk_plush"))

        // Then
        assertTrue(resul.isSuccess)
        assertNotNull(resul.getOrNull())
        assertEquals("Elon Musk", resul.getOrNull()?.name)
    }

    @Test
    fun `When getById is called with a nonexistent ID then it should return a failure`() = runBlocking {
        // When
        val result = HardcodedProductRepositoryImpl.getById(ProductId("nonexistent_id"))

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `When getById is called with an empty ID then it should throw BlankProductIdException`() {
        // Then
        assertFailsWith<BlankProductIdException> {
            runBlocking { HardcodedProductRepositoryImpl.getById(ProductId("")) }
        }
    }
}