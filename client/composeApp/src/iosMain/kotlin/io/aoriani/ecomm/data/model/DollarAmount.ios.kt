package io.aoriani.ecomm.data.model

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.Serializable
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

/**
 * iOS-specific implementation of [DollarAmount].
 *
 * This class provides a precise representation of monetary values using [NSDecimalNumber] for calculations,
 * ensuring accuracy and avoiding floating-point inaccuracies. It supports arithmetic operations
 * and proper string formatting using [NSNumberFormatter].
 *
 * The class is [Serializable] using [DollarAmountAsStringSerializer] for seamless conversion
 * to and from string representations.
 *
 * @property delegate The underlying [NSDecimalNumber] instance.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
actual class DollarAmount {
    private val delegate: NSDecimalNumber

    private constructor(delegate: NSDecimalNumber) {
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
     *                                     or cannot be parsed as a [NSDecimalNumber].
     */
    @Throws(DollarAmountFormatException::class)
    @OptIn(ExperimentalForeignApi::class)
    actual constructor(value: String) {
        requireValidDollarAmount(value)
        try {
            delegate = NSDecimalNumber(string = value)
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
        return DollarAmount(delegate.decimalNumberByAdding(other.delegate))
    }

    /**
     * Multiplies this [DollarAmount] by an integer.
     * @param other The integer to multiply by.
     * @return A new [DollarAmount] representing the product.
     */
    @OptIn(ExperimentalForeignApi::class)
    actual operator fun times(other: Int): DollarAmount {
        val multiplier = NSDecimalNumber(other)
        return DollarAmount(delegate.decimalNumberByMultiplyingBy(multiplier))
    }

    /**
     * Returns the string representation of this [DollarAmount],
     * formatted to 2 decimal places using [NSNumberFormatterDecimalStyle].
     *
     * For example, a value representing `10.456` will be formatted as `"10.46"`, and `10` as `"10.00"`.
     */
    actual override fun toString(): String {
        val numberFormatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            usesGroupingSeparator = false
            minimumFractionDigits = 2.toULong()
            maximumFractionDigits = 2.toULong()
        }

        return numberFormatter.stringFromNumber(delegate) ?: delegate.toString()
    }

    /**
     * Checks if this [DollarAmount] is equal to another object.
     *
     * Two [DollarAmount] instances are considered equal if their underlying [NSDecimalNumber]
     * representations are numerically equal (i.e., `isEqual` returns `true`).
     * For an object to be equal to a [DollarAmount], it must also be an instance of [DollarAmount].
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
     * Returns the hash code of this [DollarAmount].
     *
     * The hash code is calculated based on the underlying [NSDecimalNumber] value.
     * This ensures that two [DollarAmount] instances considered equal by the [equals] method
     * will have the same hash code, promoting correct behavior in hash-based collections.
     *
     * @return The hash code.
     */
    actual override fun hashCode(): Int {
        return delegate.hash().toInt()
    }
}
