package dev.aoriani.ecomm.config

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import java.sql.Connection
import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

class DatabaseConfigTest {

    @Test
    fun `When database is configured then transaction isolation level is serializable`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ecomm.database.url" to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                "ecomm.database.driver" to "org.h2.Driver",
                "ecomm.images.base-url" to "http://localhost:8080/static/images",
            )
        }
        application { configureDatabase() }

        assertEquals(Connection.TRANSACTION_SERIALIZABLE, TransactionManager.manager.defaultIsolationLevel)
    }
}

