package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory class for creating platform-specific SQL drivers.
 *
 * This class uses Kotlin's `expect`/`actual` mechanism to provide a platform-agnostic
 * way to obtain an `SqlDriver` instance. The actual implementation will be provided
 * by platform-specific modules (e.g., Android, iOS).
 */
expect class SqlDriverFactory {
    /**
     * Creates and returns a platform-specific SQL driver.
     *
     * This function is responsible for instantiating the appropriate `SqlDriver`
     * implementation based on the target platform (e.g., Android, iOS, JVM).
     *
     * @return An instance of [SqlDriver] suitable for the current platform.
     */
    fun createDriver(): SqlDriver
}