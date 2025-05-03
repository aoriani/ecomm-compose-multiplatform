package dev.aoriani.ecomm.graphql.models

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val images: List<String>,
    val material: String,
    val inStock: Boolean,
    val countryOfOrigin: String,
)