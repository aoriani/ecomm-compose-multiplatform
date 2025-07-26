package io.aoriani.ecomm.data.model

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.Serializable
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

/**
 * iOS-specific implementation of [DollarAmount].
 *
 * Uses [NSDecimalNumber] for calculations and [NSNumberFormatter] for string conversion.
 *
 * @property delegate The underlying [NSDecimalNumber] instance.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
actual class DollarAmount private constructor(private val delegate: NSDecimalNumber) {
    /**
     * Creates a [DollarAmount] from a string value.
     * @param value The string representation of the dollar amount.
     */
    @OptIn(ExperimentalForeignApi::class)
    actual constructor(value: String) : this(NSDecimalNumber(value))

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
     * formatted to 2 decimal places.
     */
    actual override fun toString(): String {
        val numberFormatter = NSNumberFormatter()
        numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
        numberFormatter.minimumFractionDigits = 2.toULong()
        numberFormatter.maximumFractionDigits = 2.toULong()

        return numberFormatter.stringFromNumber(delegate) ?: delegate.toString()
    }

    /**
     * Checks if this [DollarAmount] is equal to another object.
     * @param other The object to compare with.
     * @return `true` if the objects are equal, `false` otherwise.
     */
    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other !is DollarAmount) return false
        return delegate.isEqual(other.delegate)
    }

    /**
     * Returns the hash code of this [DollarAmount].
     * @return The hash code.
     */
    actual override fun hashCode(): Int {
        return delegate.hash().toInt()
    }

}
