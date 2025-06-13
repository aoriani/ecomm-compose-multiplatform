package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.util.Properties

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(
            url = "jdbc:sqlite:test.db",
            properties = Properties(),
            schema = ProductDatabase.Schema.synchronous()
        )
    }
}