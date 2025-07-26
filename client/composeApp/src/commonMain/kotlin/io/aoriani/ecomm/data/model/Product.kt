package io.aoriani.ecomm.data.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

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
): ProductBasic {
    override val thumbnailUrl: String? get() = images.firstOrNull()
}