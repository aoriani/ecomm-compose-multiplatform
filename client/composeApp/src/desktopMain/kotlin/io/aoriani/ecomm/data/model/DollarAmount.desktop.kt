package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Desktop-specific implementation of [DollarAmount].
 *
 * This class provides a precise representation of monetary values using [BigDecimal] for calculations,
 * ensuring accuracy and avoiding floating-point inaccuracies. It supports arithmetic operations
 * and proper string formatting.
 *
 * The class is [Serializable] using [DollarAmountAsStringSerializer] for seamless conversion
 * to and from string representations.
 *
 * @property delegate The underlying [BigDecimal] instance representing the monetary value.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
actual class DollarAmount {

    private val delegate: BigDecimal

    private constructor(value: BigDecimal) {
        delegate = value
    }
    /**
     * Creates a [DollarAmount] from a string value.
     *
     * The input string must conform to the [DOLLAR_AMOUNT_REGEX] pattern,
     * typically representing a numeric value with an optional decimal part.
     *
     * @param value The string representation of the dollar amount (e.g., "10.00", "5", "123.45").
     * @throws DollarAmountFormatException if the input string is not a valid dollar amount format
     *                                     or cannot be parsed as a [BigDecimal].
     */
    @Throws(DollarAmountFormatException::class)
    actual constructor(value: String) {
        requireValidDollarAmount(value)
        try {
            delegate = BigDecimal(value)
        } catch (_: NumberFormatException) {
            throw DollarAmountFormatException("Invalid dollar amount format: $value")
        }
    }

    /**
     * Adds another [DollarAmount] to this one.
     * @param other The [DollarAmount] to add.
     * @return A new [DollarAmount] representing the sum of the two amounts.
     */
    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(delegate.add(other.delegate))
    }

    /**
     * Multiplies this [DollarAmount] by an integer.
     * @param other The integer to multiply by.
     * @return A new [DollarAmount] representing the product.
     */
    actual operator fun times(other: Int): DollarAmount {
        return DollarAmount(delegate.multiply(BigDecimal(other)))
    }

    /**
     * Returns the string representation of this [DollarAmount],
     * rounded to 2 decimal places using half-even rounding ([RoundingMode.HALF_EVEN]).
     *
     * For example, a value of `10.456` will be represented as `"10.46"`, and `10` as `"10.00"`.
     */
    actual override fun toString(): String {
        return delegate.setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    /**
     * Checks if this [DollarAmount] is equal to another object.
     *
     * Two [DollarAmount] instances are considered equal if their underlying [BigDecimal]
     * representations are numerically equal (i.e., `compareTo` returns 0), regardless of their scale.
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
     * The hash code is calculated based on the underlying [BigDecimal] value after stripping trailing zeros.
     * This ensures that two [DollarAmount] instances considered equal by the [equals] method
     * (i.e., having numerically equal [BigDecimal] values) will have the same hash code,
     * promoting correct behavior in hash-based collections.
     *
     * @return The hash code for this dollar amount.
     */
    actual override fun hashCode(): Int {
        return toString().hashCode()
    }
}