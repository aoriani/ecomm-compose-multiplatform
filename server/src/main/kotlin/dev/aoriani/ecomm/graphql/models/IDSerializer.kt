package dev.aoriani.ecomm.graphql.models

import com.expediagroup.graphql.generator.scalars.ID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IDSerializer : KSerializer<ID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.expediagroup.graphql.generator.scalars.ID",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: ID) = encoder.encodeString(value.value)

    override fun deserialize(decoder: Decoder): ID = ID(decoder.decodeString())
}