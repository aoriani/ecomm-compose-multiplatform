package dev.aoriani.ecomm.domain.models

import java.math.BigDecimal

/**
 * Represents a product available in the e-commerce catalog.
 * @property id Unique identifier of the product
 * @property name Name of the product
 * @property price Price of the product in USD
 * @property description Detailed description of the product
 * @property images List of image URLs associated with the product
 * @property material Material composition of the product
 * @property inStock Indicates whether the product is currently in stock
 * @property countryOfOrigin Country where the product was manufactured
 */
data class Product(
    val id: ProductId,
    val name: String,
    val price: BigDecimal,
    val description: String,
    val images: List<String>,
    val material: String,
    val inStock: Boolean,
    val countryOfOrigin: String
)