package io.aoriani.ecomm.graphql.models

data class Product(
    val id: String,
    val title: String,
    val price: Double,
    val description: String,
    val images: List<String>,
    val categoryName: String
)