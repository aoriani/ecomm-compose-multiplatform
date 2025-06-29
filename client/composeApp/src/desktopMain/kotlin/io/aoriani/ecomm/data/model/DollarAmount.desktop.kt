package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
actual class DollarAmount private constructor(private val delegate: BigDecimal) {
    actual constructor(value: String) : this(BigDecimal(value))

    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(delegate.add(other.delegate))
    }

    actual operator fun times(other: Int): DollarAmount {
        return DollarAmount(delegate.multiply(BigDecimal(other)))
    }

    actual override fun toString(): String {
        return delegate.setScale(2, RoundingMode.HALF_EVEN).toString()
    }
}