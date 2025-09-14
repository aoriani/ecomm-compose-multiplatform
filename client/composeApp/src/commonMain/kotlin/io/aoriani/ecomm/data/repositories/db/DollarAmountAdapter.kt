package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.ColumnAdapter
import io.aoriani.ecomm.data.model.DollarAmount

/**
 * A [ColumnAdapter] for converting [DollarAmount] objects to and from their String representation
 * for storage in the database.
 *
 * This adapter is necessary because SQLDelight does not natively support custom types like [DollarAmount].
 *
 * - `decode`: Converts a String value retrieved from the database into a [DollarAmount] object.
 * - `encode`: Converts a [DollarAmount] object into its String representation for database storage.
 */
class DollarAmountAdapter : ColumnAdapter<DollarAmount, String> {
    /**
     * Decodes a String representation of a dollar amount from the database into a [DollarAmount] object.
     *
     * @param databaseValue The String value retrieved from the database.
     * @return A [DollarAmount] object representing the decoded value.
     */
    override fun decode(databaseValue: String): DollarAmount = DollarAmount(databaseValue)
    /**
     * Converts a DollarAmount object to its String representation.
     *
     * This function is used by SQLDelight to store DollarAmount objects in the database
     * as strings.
     *
     * @param value The DollarAmount object to encode.
     * @return The String representation of the DollarAmount.
     */
    override fun encode(value: DollarAmount): String = value.toString()
}