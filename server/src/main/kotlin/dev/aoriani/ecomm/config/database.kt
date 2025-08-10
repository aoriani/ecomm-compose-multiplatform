package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.data.database.initializeDatabaseAndSeedIfEmpty
import dev.aoriani.ecomm.data.repositories.DatabaseProductRepositoryImpl
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.sql.Connection

/**
 * Configures the database connection for the application and initializes the database schema.
 *
 * This method retrieves the database URL and driver details from the application's configuration settings.
 * It establishes a connection to the database and sets the default isolation level for transactions to `TRANSACTION_SERIALIZABLE`.
 * Additionally, it ensures the database schema is initialized and seeds the database with data if required.
 *
 * This function is typically invoked during the application startup process as part of configuring the application's resources.
 */
internal fun Application.configureDatabase() {
    dependencies {
        provide<ProductRepository> { DatabaseProductRepositoryImpl }
        provide<GetAllProductsUseCase> { GetAllProductsUseCase(DatabaseProductRepositoryImpl) }
        provide<GetProductByIdUseCase> { GetProductByIdUseCase(DatabaseProductRepositoryImpl) }
    }
    val dbUrl = environment.config.property("ecomm.database.url").getString()
    val dbDriver = environment.config.property("ecomm.database.driver").getString()
    Database.connect(dbUrl, dbDriver)
    TransactionManager.manager.defaultIsolationLevel =
        Connection.TRANSACTION_SERIALIZABLE // Or configure this too if needed
    val imageUrlBase = environment.config.property("ecomm.images.base-url").getString()
    initializeDatabaseAndSeedIfEmpty(imageUrlBase)
}