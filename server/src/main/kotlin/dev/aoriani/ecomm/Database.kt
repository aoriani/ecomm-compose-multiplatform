package dev.aoriani.ecomm

import dev.aoriani.ecomm.repository.database.initializeDatabaseAndSeedIfEmpty
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.sql.Connection

/**
 * Configures the database connection using settings from application.conf.
 * Initializes the database schema and seeds data if the database is empty.
 */
fun Application.configureDatabase() {
    val dbUrl = environment.config.property("ecomm.database.url").getString()
    val dbDriver = environment.config.property("ecomm.database.driver").getString()
    Database.connect(dbUrl, dbDriver)
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or configure this too if needed
    initializeDatabaseAndSeedIfEmpty()
}