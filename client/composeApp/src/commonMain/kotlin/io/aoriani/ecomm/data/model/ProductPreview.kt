package io.aoriani.ecomm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductPreview(
    override val id: ProductBasic.Id,
    override val name: String,
    override val price: DollarAmount,
    override val thumbnailUrl: String?,
): ProductBasic