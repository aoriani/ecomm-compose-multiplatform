package io.aoriani.ecomm.data.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

/**
 * Represents a detailed product in the e-commerce application.
 *
 * This class extends [ProductBasic] to include more comprehensive information
 * about a product.
 *
 * @property id The unique identifier of the product. Inherited from [ProductBasic].
 * @property name The name of the product. Inherited from [ProductBasic].
 * @property price The price of the product as a [DollarAmount]. Inherited from [ProductBasic].
 * @property description A detailed description of the product.
 * @property material The primary material the product is made of.
 * @property countryOfOrigin The country where the product was manufactured or originated from.
 * @property inStock A boolean indicating whether the product is currently in stock.
 * @property images An immutable list of URLs pointing to images of the product.
 */
@Serializable
data class Product(
    override val id: ProductBasic.Id,
    override val name: String,
    override val price: DollarAmount,
    val description: String,
    val material: String,
    val countryOfOrigin: String,
    val inStock: Boolean,
    val images: ImmutableList<String>,
) : ProductBasic {
    /** The URL of the thumbnail image for the product, typically the first image in the [images] list. Inherited from [ProductBasic]. */
    override val thumbnailUrl: String? get() = images.firstOrNull()
}
