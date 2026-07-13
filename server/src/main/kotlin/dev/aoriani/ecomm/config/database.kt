package dev.aoriani.ecomm.config

import dev.aoriani.ecomm.data.database.initializeDatabaseAndSeed
import dev.aoriani.ecomm.data.repositories.DatabaseProductRepositoryImpl
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import dev.aoriani.ecomm.domain.usecases.GetAllProductsUseCase
import dev.aoriani.ecomm.domain.usecases.GetProductByIdUseCase
import io.ktor.server.application.Application
import io.ktor.server.config.getAs
import io.ktor.server.plugins.di.dependencies
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import java.sql.Connection

/**
 * Configure database connection and DI bindings.
 *
 * - Registers DI providers for domain components using Ktor's DI plugin:
 *   `ProductRepository`, `GetAllProductsUseCase`, `GetProductByIdUseCase`.
 * - Deserializes the `ecomm.database` and `ecomm.images` configuration sections
 *   into typed settings and connects via Exposed `Database.connect`.
 * - Sets Exposed's default transaction isolation to `TRANSACTION_SERIALIZABLE`
 *   (adjust based on your database and throughput needs).
 * - Initializes the schema and idempotently upserts the initial catalog using
 *   `initializeDatabaseAndSeed`, parameterized by `ecomm.images.base-url`.
 *
 * Call during application startup before handling requests.
 */
internal fun Application.configureDatabase() {
    val databaseSettings = environment.config.property("ecomm.database").getAs<DatabaseSettings>()
    val imagesSettings = environment.config.property("ecomm.images").getAs<ImagesSettings>()
    val database = Database.connect(
        url = databaseSettings.url,
        driver = databaseSettings.driver,
        databaseConfig = DatabaseConfig {
            defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        },
    )

    dependencies {
        provide<Database> { database }
        provide<ProductRepository> {
            DatabaseProductRepositoryImpl(
                database = resolve<Database>(),
                ioDispatcher = Dispatchers.IO,
            )
        }
        provide<GetAllProductsUseCase> { GetAllProductsUseCase(resolve<ProductRepository>()) }
        provide<GetProductByIdUseCase> { GetProductByIdUseCase(resolve<ProductRepository>()) }
    }

    initializeDatabaseAndSeed(database, imagesSettings.baseUrl)
}
