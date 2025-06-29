package io.aoriani.ecomm.data.model

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DollarAmount private constructor(private val delegate: NSDecimalNumber) {
    @OptIn(ExperimentalForeignApi::class)
    actual constructor(value: String) : this(NSDecimalNumber(value))

    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(delegate.decimalNumberByAdding(other.delegate))
    }

    @OptIn(ExperimentalForeignApi::class)
    actual operator fun times(other: Int): DollarAmount {
        val multiplier = NSDecimalNumber(other)
        return DollarAmount(delegate.decimalNumberByMultiplyingBy(multiplier))
    }

    actual override fun toString(): String {
        val numberFormatter = NSNumberFormatter()
        numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
        numberFormatter.minimumFractionDigits = 2.toULong()
        numberFormatter.maximumFractionDigits = 2.toULong()

        return numberFormatter.stringFromNumber(delegate) ?: delegate.toString()
    }
}
