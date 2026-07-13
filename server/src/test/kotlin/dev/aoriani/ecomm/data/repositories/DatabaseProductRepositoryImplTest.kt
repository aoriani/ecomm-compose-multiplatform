package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.data.database.ProductTable
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DatabaseProductRepositoryImplTest {

    @Test
    fun `getAll returns an empty list when the product table is empty`() = runTest {
        val database = createDatabase(createSchema = true)
        val repository = DatabaseProductRepositoryImpl(database, StandardTestDispatcher(testScheduler))

        val result = repository.getAll()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `getAll returns products mapped from database rows`() = runTest {
        val database = createDatabase(createSchema = true)
        insertProduct(database)
        val repository = DatabaseProductRepositoryImpl(database, StandardTestDispatcher(testScheduler))

        val result = repository.getAll()

        assertTrue(result.isSuccess)
        val product = result.getOrThrow().single()
        assertEquals(ProductId("test-product"), product.id)
        assertEquals("Test Product", product.name)
        assertEquals(BigDecimal("19.99"), product.price)
        assertEquals(listOf("https://example.com/product.png"), product.images)
        assertEquals(false, product.inStock)
    }

    @Test
    fun `getById returns the matching product`() = runTest {
        val database = createDatabase(createSchema = true)
        insertProduct(database)
        val repository = DatabaseProductRepositoryImpl(database, StandardTestDispatcher(testScheduler))

        val result = repository.getById(ProductId("test-product"))

        assertTrue(result.isSuccess)
        assertEquals("Test Product", result.getOrThrow().name)
        assertEquals("Test material", result.getOrThrow().material)
        assertEquals("Test Country", result.getOrThrow().countryOfOrigin)
    }

    @Test
    fun `getById returns ProductNotFoundException when the product does not exist`() = runTest {
        val database = createDatabase(createSchema = true)
        val repository = DatabaseProductRepositoryImpl(database, StandardTestDispatcher(testScheduler))

        val result = repository.getById(ProductId("missing"))

        assertTrue(result.isFailure)
        assertIs<ProductNotFoundException>(result.exceptionOrNull())
    }

    @Test
    fun `getAll returns failure when the database query fails`() = runTest {
        val database = createDatabase(createSchema = false)
        val repository = DatabaseProductRepositoryImpl(database, StandardTestDispatcher(testScheduler))

        val result = repository.getAll()

        assertTrue(result.isFailure)
    }

    private fun createDatabase(createSchema: Boolean): Database {
        val database = Database.connect(
            url = "jdbc:h2:mem:repository-${UUID.randomUUID()};DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
        )
        if (createSchema) {
            transaction(db = database) {
                SchemaUtils.create(ProductTable)
            }
        }
        return database
    }

    private fun insertProduct(database: Database) {
        transaction(db = database) {
            ProductTable.insert {
                it[id] = "test-product"
                it[name] = "Test Product"
                it[price] = BigDecimal("19.99")
                it[description] = "Test description"
                it[images] = listOf("https://example.com/product.png")
                it[material] = "Test material"
                it[inStock] = false
                it[countryOfOrigin] = "Test Country"
            }
        }
    }
}
