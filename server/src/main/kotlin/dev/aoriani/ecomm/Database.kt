package dev.aoriani.ecomm

import dev.aoriani.ecomm.repository.database.ProductTable
import dev.aoriani.ecomm.repository.database.initialize
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.sql.Connection

fun Application.configureDatabase() {
        val db = Database.connect("jdbc:sqlite:./data/products.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        ProductTable.initialize()
}