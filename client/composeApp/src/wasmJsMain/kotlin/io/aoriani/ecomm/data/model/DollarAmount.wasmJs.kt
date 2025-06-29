package io.aoriani.ecomm.data.model


@JsModule("big.js")
private external class Big {
    constructor(value: String)
    constructor(value: Int)

    fun plus(other: Big): Big
    fun times(other: Big): Big

    fun toFixed(decimals: Int, roundingMode: Int): String

    companion object {
        val roundHalfEven: Int
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DollarAmount private constructor(private val delegate: Big) {
    actual constructor(value: String) : this(Big(value))

    actual operator fun plus(other: DollarAmount): DollarAmount {
        return DollarAmount(delegate.plus(other.delegate))
    }

    actual operator fun times(other: Int): DollarAmount {
        return DollarAmount(delegate.times(Big(other)))
    }

    actual override fun toString(): String {
        return delegate.toFixed(2, Big.roundHalfEven)
    }
}
