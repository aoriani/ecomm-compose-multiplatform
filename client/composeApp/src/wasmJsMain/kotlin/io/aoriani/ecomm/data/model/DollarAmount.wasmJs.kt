package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable

/**
 * WasmJs-specific implementation of [DollarAmount].
 *
 * This class provides a precise representation of monetary values using the `big.js` library for calculations,
 * ensuring accuracy and avoiding floating-point inaccuracies. It supports arithmetic operations
 * and proper string formatting.
 *
 * The class is [Serializable] using [DollarAmountAsStringSerializer] for seamless conversion
 * to and from string representations.
 *
 * @property delegate The underlying `Big` instance.
 */
@OptIn(ExperimentalWasmJsInterop::class)
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
actual class DollarAmount {
    private val delegate: Big

    /**
     * Private constructor for internal use, allowing creation of [DollarAmount] from a `Big` instance.
     * @param delegate The `Big` instance representing the dollar amount.
     */
    private constructor(delegate: Big) {
        this.delegate = delegate
    }

    /**
     * Creates a [DollarAmount] from a string value.
     *
     * The input string must conform to the [DOLLAR_AMOUNT_REGEX] pattern,
     * typically representing a numeric value with an optional decimal part.
     *
     * @param value The string representation of the dollar amount (e.g., "10.00", "5", "123.45").
     * @throws DollarAmountFormatException if the input string is not a valid dollar amount format
     *                                     or cannot be parsed by `Big.js`.
     */
    actual constructor(value: String) {
        requireValidDollarAmount(value)
        try {
            delegate = Big(value)
        } catch (_: Throwable) {
            throw DollarAmountFormatException("Invalid dollar amount format: $value")
        }
    }

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
     * rounded to 2 decimal places using the `Big.js` half-even rounding mode ([Big.roundHalfEven]).
     *
     * For example, a value representing `10.456` will be formatted as `"10.46"`, and `10` as `"10.00"`.
     */
    actual override fun toString(): String {
        val returnString =  delegate.toFixed(2, Big.roundHalfEven)

        // Zero will always be positive to ensure consistent behavior among platforms
        return if (returnString == "-0.00") "0.00" else returnString
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
        return this.toString() == other.toString()
    }

    /**
     * Returns the hash code for this [DollarAmount].
     *
     * The hash code is calculated based on the string representation of its underlying `Big.js` value.
     * This ensures that two [DollarAmount] instances considered equal by the [equals] method
     * will have the same hash code, promoting correct behavior in hash-based collections.
     *
     * @return The hash code for this dollar amount.
     */
    actual override fun hashCode(): Int {
        // The most straightforward way to ensure consistency with an `eq` method
        // from an external JS library is to base the hashCode on the canonical string representation.
        return delegate.toString().hashCode()
    }
}
