package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable

/**
 * WasmJs-specific implementation of [DollarAmount].
 *
 * Uses the `big.js` library for calculations.
 *
 * @property delegate The underlying `Big` instance.
 */
@JsModule("big.js")
private external class Big {
    constructor(value: String)
    constructor(value: Int)

    fun plus(other: Big): Big
    fun times(other: Big): Big

    fun toFixed(decimals: Int, roundingMode: Int): String

    companion object {
        val roundHalfEven: Int
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
actual class DollarAmount private constructor(private val delegate: Big) {
    /**
     * Creates a [DollarAmount] from a string value.
     * @param value The string representation of the dollar amount.
     */
    actual constructor(value: String) : this(Big(value))

    /**
     * Adds another [DollarAmount] to this one.
     * @param other The [DollarAmount] to add.
     * @return A new [DollarAmount] representing the sum.
     */
    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(delegate.plus(other.delegate))
    }

    /**
     * Multiplies this [DollarAmount] by an integer.
     * @param other The integer to multiply by.
     * @return A new [DollarAmount] representing the product.
     */
    actual operator fun times(other: Int): DollarAmount {
        return DollarAmount(delegate.times(Big(other)))
    }

    /**
     * Returns the string representation of this [DollarAmount],
     * rounded to 2 decimal places using half-even rounding.
     */
    actual override fun toString(): String {
        return delegate.toFixed(2, Big.roundHalfEven)
    }
}
