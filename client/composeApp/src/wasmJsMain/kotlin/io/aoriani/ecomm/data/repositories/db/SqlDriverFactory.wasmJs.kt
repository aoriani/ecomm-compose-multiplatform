package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

private val sqlJsWorkerUrl: String = js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(
            Worker(sqlJsWorkerUrl)
        ).also {
            ProductDatabase.Schema.create(it)
        }
    }
}