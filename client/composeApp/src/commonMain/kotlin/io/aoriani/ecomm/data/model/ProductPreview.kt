package io.aoriani.ecomm.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ProductPreview(
    val id: String,
    val name: String,
    val price: DollarAmount,
    val thumbnailUrl: String?,
)