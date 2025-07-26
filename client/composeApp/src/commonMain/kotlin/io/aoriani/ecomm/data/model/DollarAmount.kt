package io.aoriani.ecomm.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a dollar amount.
 *
 * This is an expect class, meaning it declares a type that must be implemented
 * by actual classes in platform-specific source sets.
 *
 * @property value The string representation of the dollar amount.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
expect class DollarAmount(value: String) {
    /**
     * Adds another [DollarAmount] to this one.
     * @param other The [DollarAmount] to add.
     * @return A new [DollarAmount] representing the sum.
     */
    operator fun plus(other: DollarAmount): DollarAmount

    /**
     * Multiplies this [DollarAmount] by an integer.
     * @param other The integer to multiply by.
     * @return A new [DollarAmount] representing the product.
     */
    operator fun times(other: Int): DollarAmount

    /**
     * Returns the string representation of this [DollarAmount].
     */
    override fun toString(): String

    /**
     * Checks if this [DollarAmount] is equal to another object.
     * Two [DollarAmount] instances are considered equal if their string representations are equal.
     * @param other The object to compare with.
     * @return `true` if the objects are equal, `false` otherwise.
     */
    override fun equals(other: Any?): Boolean
}

/**
 * A [DollarAmount] instance representing zero dollars.
 */
val DollarAmount.Companion.ZERO: DollarAmount get() = DollarAmount("0")

/**
 * Custom KSerializer for [DollarAmount].
 * Serializes and deserializes [DollarAmount] as a string.
 */
class DollarAmountAsStringSerializer : KSerializer<DollarAmount> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.aoriani.ecomm.data.model.DollarAmount", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: DollarAmount
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DollarAmount {
        return DollarAmount(decoder.decodeString())
    }

}
