package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import co.touchlab.kermit.Logger
import org.w3c.dom.Worker

//private val sqlJsWorkerUrl: String
//    get() = js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        return createDefaultWebWorkerDriver()

//        Logger.i("ORIANI") { "createDriver -> <$sqlJsWorkerUrl>" }
//        return WebWorkerDriver(
//            Worker(sqlJsWorkerUrl)
//        ).also {
//            ProductDatabase.Schema.create(it)
//        }
    }
}