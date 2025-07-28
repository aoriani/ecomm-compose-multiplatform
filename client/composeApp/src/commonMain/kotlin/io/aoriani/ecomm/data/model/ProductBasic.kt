package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Represents the basic information of a product.
 * This interface defines the essential properties that any product representation should have.
 */
interface ProductBasic {

    /**
     * Represents the unique identifier of a product.
     *
     * This value class wraps a String to provide type safety for product IDs.
     *
     * @property value The string representation of the product ID.
     */
    @Serializable
    @JvmInline
    value class Id(val value: String)

    /** The unique identifier of the product. */
    val id: Id

    /** The name of the product. */
    val name: String

    /** The price of the product, represented as a [DollarAmount]. */
    val price: DollarAmount

    /** The URL of the product's thumbnail image. This can be null if no thumbnail is available. */
    val thumbnailUrl: String?
}
