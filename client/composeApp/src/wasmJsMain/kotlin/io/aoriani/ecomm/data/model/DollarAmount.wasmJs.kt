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

    fun eq(other: Big): Boolean

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

    /**
     * Checks if this [DollarAmount] is equal to another object.
     *
     * Two [DollarAmount] instances are considered equal if their underlying `Big.js`
     * representations are numerically equal (as determined by `Big.eq()`).
     * For an object to be equal to a [DollarAmount], it must also be
     * an instance of [DollarAmount].
     *
     * @param other The object to compare with.
     * @return `true` if `other` is a [DollarAmount] representing the same monetary value as this instance,
     *         `false` otherwise.
     */
    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other !is DollarAmount) return false
        return delegate.eq(other.delegate)
    }

    /**
     * Returns the hash code for this [DollarAmount].
     *
     * The hash code is calculated based on the string representation of its underlying `Big.js` value.
     * This ensures that two [DollarAmount] instances considered equal by the [equals] method
     * will have the same hash code.
     *
     * @return The hash code for this dollar amount.
     */
    actual override fun hashCode(): Int {
        // The most straightforward way to ensure consistency with an `eq` method
        // from an external JS library is to base the hashCode on the canonical string representation.
        return delegate.toString().hashCode()
    }
}
