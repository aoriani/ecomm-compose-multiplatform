package io.aoriani.ecomm.data.model

import kotlinx.collections.immutable.ImmutableList

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val images: ImmutableList<String>,
)