package io.aoriani.ecomm.data.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: DollarAmount,
    val description: String,
    val images: ImmutableList<String>,
)