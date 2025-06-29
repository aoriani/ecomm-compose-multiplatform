package io.aoriani.ecomm.data.model

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DollarAmount(value: String) {
    operator fun plus(other: DollarAmount): DollarAmount
    operator fun times(other: Int): DollarAmount
    override fun toString(): String
}