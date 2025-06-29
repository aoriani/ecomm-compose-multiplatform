package io.aoriani.ecomm.data.model


@JsModule("big.js")
private external class Big {
    constructor(value: String)
    constructor(value: Int)

    fun plus(other: Big): Big
    fun times(other: Big): Big
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DollarAmount private constructor(private val rawValue: Big) {
    actual constructor(value: String) : this(Big(value))

    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(rawValue.plus(other.rawValue))
    }

    actual operator fun times(other: Int): DollarAmount {
        return DollarAmount(rawValue.times(Big(other)))
    }

}
