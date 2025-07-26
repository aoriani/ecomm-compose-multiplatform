package io.aoriani.ecomm.data.model

/**
 * Represents the basic information of a product.
 * This interface defines the essential properties that any product representation should have.
 */
interface ProductBasic {
    /**
     * Type alias for the product identifier, which is a String.
     */
    typealias Id = String

    /** The unique identifier of the product. */
    val id: Id

    /** The name of the product. */
    val name: String

    /** The price of the product, represented as a [DollarAmount]. */
    val price: DollarAmount

    /** The URL of the product's thumbnail image. This can be null if no thumbnail is available. */
    val thumbnailUrl: String?
}
