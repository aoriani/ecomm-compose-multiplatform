package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver

actual class SqlDriverFactory {
    //TODO : default web work driver is memory-only
    // investigate a IndexDB or OPFS implementation
    actual fun createDriver(): SqlDriver {
        val sqlDriver = createDefaultWebWorkerDriver()
        // TODO - Wasm is assync so this need to be in a coroutine or something
        //ProductDatabase.Schema.awaitCreate(sqlDriver)
        return sqlDriver
    }
}