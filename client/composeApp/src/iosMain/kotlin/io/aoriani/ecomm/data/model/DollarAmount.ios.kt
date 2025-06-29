package io.aoriani.ecomm.data.model

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDecimalNumber

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DollarAmount private constructor(private val rawValue: NSDecimalNumber) {
    @OptIn(ExperimentalForeignApi::class)
    actual constructor(value: String) : this(NSDecimalNumber(value))

    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(rawValue.decimalNumberByAdding(other.rawValue))
    }

    @OptIn(ExperimentalForeignApi::class)
    actual operator fun times(other: Int): DollarAmount {
        val multiplier = NSDecimalNumber(other)
        return DollarAmount(rawValue.decimalNumberByMultiplyingBy(multiplier))
    }
}
