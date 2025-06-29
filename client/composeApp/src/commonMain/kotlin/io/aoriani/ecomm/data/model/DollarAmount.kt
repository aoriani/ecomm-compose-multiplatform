package io.aoriani.ecomm.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
expect class DollarAmount(value: String) {
    operator fun plus(other: DollarAmount): DollarAmount
    operator fun times(other: Int): DollarAmount
    override fun toString(): String
}

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
