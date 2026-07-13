package dev.aoriani.ecomm.config

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.Connection
import kotlin.test.Test
import kotlin.test.assertEquals

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
        application {
            configureDatabase()
            val configuredApplication = this
            routing {
                get("/test/database-isolation") {
                    val database: Database by configuredApplication.dependencies
                    val isolationLevel = transaction(db = database) {
                        connection.transactionIsolation
                    }
                    call.respondText(isolationLevel.toString())
                }
            }
        }

        val response = client.get("/test/database-isolation")

        assertEquals(Connection.TRANSACTION_SERIALIZABLE.toString(), response.bodyAsText())
    }
}
