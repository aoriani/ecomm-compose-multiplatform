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
 * Configure database connection and DI bindings.
 *
 * - Registers DI providers for domain components using Ktor's DI plugin:
 *   `ProductRepository`, `GetAllProductsUseCase`, `GetProductByIdUseCase`.
 * - Reads `ecomm.database.url` and `ecomm.database.driver` from config and
 *   connects via Exposed `Database.connect`.
 * - Sets Exposed's default transaction isolation to `TRANSACTION_SERIALIZABLE`
 *   (adjust based on your database and throughput needs).
 * - Initializes the schema and seeds initial data when empty using
 *   `initializeDatabaseAndSeedIfEmpty`, parameterized by `ecomm.images.base-url`.
 *
 * Call during application startup before handling requests.
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
        Connection.TRANSACTION_SERIALIZABLE // Tune per DB/consistency requirements
    val imageUrlBase = environment.config.property("ecomm.images.base-url").getString()
    initializeDatabaseAndSeedIfEmpty(imageUrlBase)
}
