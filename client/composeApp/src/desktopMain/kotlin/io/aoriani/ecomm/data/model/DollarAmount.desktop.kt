package io.aoriani.ecomm.data.model

import java.math.BigDecimal

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DollarAmount private constructor(private val rawValue: BigDecimal) {
    actual constructor(value: String) : this(BigDecimal(value))

    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(rawValue.add(other.rawValue))
    }

    actual operator fun times(other: Int): DollarAmount {
        return DollarAmount(rawValue.multiply(BigDecimal(other)))
    }
}