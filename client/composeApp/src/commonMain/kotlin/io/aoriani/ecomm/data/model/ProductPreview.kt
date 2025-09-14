package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a concise summary of a product, typically used in listings or previews.
 *
 * This data class implements the [ProductBasic] interface, providing essential product information.
 * It is marked as `@Serializable` for compatibility with Kotlinx Serialization.
 *
 * @property id The unique identifier of the product, of type [ProductBasic.Id].
 * @property name The name of the product.
 * @property price The price of the product, represented as a [DollarAmount].
 * @property thumbnailUrl An optional URL pointing to a thumbnail image for the product.
 */
@Serializable
data class ProductPreview(
    override val id: ProductBasic.Id,
    override val name: String,
    override val price: DollarAmount,
    override val thumbnailUrl: String?,
): ProductBasic