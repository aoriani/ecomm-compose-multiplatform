package dev.aoriani.ecomm.data.database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductTableTest {

    @Test
    fun `initialization restores a partially seeded catalog without duplicates`() {
        val database = Database.connect(
            url = "jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
        )

        initializeDatabaseAndSeed(database, "http://localhost/images")
        val initialCount = transaction(db = database) { ProductEntity.all().count() }

        transaction(db = database) {
            ProductTable.deleteWhere { ProductTable.id eq "elon_musk_plush" }
        }
        initializeDatabaseAndSeed(database, "http://localhost/images")

        val restoredCount = transaction(db = database) { ProductEntity.all().count() }
        assertEquals(16L, initialCount)
        assertEquals(initialCount, restoredCount)
    }
}
