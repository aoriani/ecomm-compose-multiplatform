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
 * The constructor accepts a string representation of a dollar amount.
 * It is expected to handle various valid formats (e.g., "10", "10.00", "10.5", "1234.56")
 * and throw a [DollarAmountFormatException] for invalid input.
 *
 * @param value The string representation of the dollar amount.
 * @throws DollarAmountFormatException if the provided string `value` is not a valid representation of a dollar amount.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = DollarAmountAsStringSerializer::class)
expect class DollarAmount @Throws(DollarAmountFormatException::class) constructor(value: String) {
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
     * This should be a canonical representation, suitable for equality checks and hashing.
     * For example, it might always include two decimal places (e.g., "10.00", "123.45").
     */
    override fun toString(): String

    /**
     * Checks if this [DollarAmount] is equal to another object.
     *
     * Two [DollarAmount] instances are considered equal if they represent the same monetary value.
     * This is determined by comparing their canonical string representations (typically, the value
     * returned by [toString]). For an object to be equal to a [DollarAmount], it must also be
     * an instance of [DollarAmount].
     *
     * @param other The object to compare with.
     * @return `true` if `other` is a [DollarAmount] representing the same monetary value as this instance,
     *         `false` otherwise.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Returns the hash code for this [DollarAmount].
     *
     * The hash code is calculated based on its canonical string representation (typically, the value
     * returned by [toString]), ensuring that two [DollarAmount] instances considered equal by
     * the [equals] method will have the same hash code.
     *
     * @return The hash code for this dollar amount.
     */
    override fun hashCode(): Int
}

/**
 * Regular expression for validating dollar amount strings.
 *
 * This regex matches strings that:
 * - Optionally start with a minus sign (`^-?`).
 * - Followed by one or more digits (`\d+`).
 * - Optionally followed by a decimal point and one or more digits (`(\.\d+)?$`).
 *   Note: While the regex allows more than two decimal places for flexibility in initial parsing,
 *   the `DollarAmount` constructor and its `toString()` method are expected to enforce
 *   or normalize to two decimal places for canonical representation.
 *
 * Examples of valid strings (according to the regex): "10", "10.0", "10.00", "1234.56", "-10", "-10.50", "10.123"
 * Examples of invalid strings: "10.", ".50", "abc", "--10"
 */
private val DOLLAR_AMOUNT_REGEX = Regex("""^(-)?\d+(\.\d+)?$""")

/**
 * Checks if the provided string `value` is a valid representation of a dollar amount.
 *
 * This function uses the [DOLLAR_AMOUNT_REGEX] to validate the input string.
 *
 * @param value The string to validate.
 * @throws DollarAmountFormatException if the `value` does not match the expected dollar amount format.
 */
@Throws(DollarAmountFormatException::class)
fun requireValidDollarAmount(value: String) {
    if (!DOLLAR_AMOUNT_REGEX.matches(value)) {
        throw DollarAmountFormatException("Invalid dollar amount format: $value")
    }
}

/**
 * Exception thrown when a string cannot be parsed into a valid [DollarAmount].
 *
 * This typically occurs if the input string does not conform to the expected format
 * for a dollar amount (e.g., "10.00", "1234.56").
 *
 * @param message A descriptive message indicating the reason for the format exception.
 */
class DollarAmountFormatException(message: String) : RuntimeException(message)

/**
 * A [DollarAmount] instance representing zero dollars.
 */
val DollarAmount.Companion.ZERO: DollarAmount get() = DollarAmount("0")

/**
 * Custom KSerializer for [DollarAmount].
 * Serializes and deserializes [DollarAmount] as a string.
 */
object DollarAmountAsStringSerializer : KSerializer<DollarAmount> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.aoriani.ecomm.data.model.DollarAmount", PrimitiveKind.STRING)

    /**
     * Serializes a [DollarAmount] object into its string representation.
     *
     * This method is called by the kotlinx.serialization framework when an object
     * containing a [DollarAmount] needs to be converted into a serialized format (e.g., JSON).
     * It uses the [DollarAmount.toString] method to get the canonical string representation
     * of the dollar amount and encodes that string.
     *
     * @param encoder The [Encoder] to write the serialized data to.
     * @param value The [DollarAmount] instance to serialize.
     */
    override fun serialize(
        encoder: Encoder,
        value: DollarAmount
    ) {
        encoder.encodeString(value.toString())
    }

    /**
     * Deserializes a [DollarAmount] from its string representation.
     *
     * This method is called by the kotlinx.serialization framework when
     * converting a JSON string (or other serialized format) back into a
     * [DollarAmount] object. It expects the input string to be a valid
     * representation of a dollar amount, as defined by the [DollarAmount]
     * constructor.
     *
     * @param decoder The decoder instance from which to read the string.
     * @return A [DollarAmount] instance created from the decoded string.
     * @throws DollarAmountFormatException if the decoded string is not a valid
     *         representation of a dollar amount.
     */
    override fun deserialize(decoder: Decoder): DollarAmount {
        return DollarAmount(decoder.decodeString())
    }
}
