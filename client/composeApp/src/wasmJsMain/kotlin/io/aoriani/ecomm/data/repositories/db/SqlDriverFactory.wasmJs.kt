package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        @Suppress("UNREACHABLE_CODE")
        return WebWorkerDriver(
            Worker(
                js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
            )
        ).also {
            ProductDatabase.Schema.create(it)
        }
    }
}