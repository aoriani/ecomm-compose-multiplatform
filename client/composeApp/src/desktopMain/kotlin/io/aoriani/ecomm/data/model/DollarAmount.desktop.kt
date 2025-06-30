package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Desktop-specific implementation of [DollarAmount].
 *
 * Uses [BigDecimal] for calculations.
 *
 * @property delegate The underlying [BigDecimal] instance.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
actual class DollarAmount private constructor(private val delegate: BigDecimal) {
    /**
     * Creates a [DollarAmount] from a string value.
     * @param value The string representation of the dollar amount.
     */
    actual constructor(value: String) : this(BigDecimal(value))

    /**
     * Adds another [DollarAmount] to this one.
     * @param other The [DollarAmount] to add.
     * @return A new [DollarAmount] representing the sum.
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
     * rounded to 2 decimal places using half-even rounding.
     */
    actual override fun toString(): String {
        return delegate.setScale(2, RoundingMode.HALF_EVEN).toString()
    }
}