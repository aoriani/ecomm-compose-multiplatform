package dev.aoriani.ecomm.graphql.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Represents a product available in the e-commerce catalog")
data class Product(
    @GraphQLDescription("Unique identifier of the product")
    val id: String,

    @GraphQLDescription("Name of the product")
    val name: String,

    @GraphQLDescription("Price of the product in USD")
    val price: Double,

    @GraphQLDescription("Detailed description of the product")
    val description: String,

    @GraphQLDescription("List of image URLs associated with the product")
    val images: List<String>,

    @GraphQLDescription("Material composition of the product")
    val material: String,

    @GraphQLDescription("Indicates whether the product is currently in stock")
    val inStock: Boolean,

    @GraphQLDescription("Country where the product was manufactured")
    val countryOfOrigin: String
)