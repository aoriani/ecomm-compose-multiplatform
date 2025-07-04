package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(ProductDatabase.Schema.synchronous(), "product.db")
    }
}