package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.db.SqlDriver

expect class SqlDriverFactory {
    fun createDriver(): SqlDriver
}