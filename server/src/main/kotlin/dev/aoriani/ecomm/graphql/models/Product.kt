package dev.aoriani.ecomm.graphql.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
@GraphQLDescription("Represents a product available in the e-commerce catalog")
data class Product(
    @property:GraphQLDescription("Unique identifier of the product")
    @Serializable(with = IDSerializer::class)
    val id: ID,

    @property:GraphQLDescription("Name of the product")
    val name: String,

    @property:GraphQLDescription("Price of the product in USD")
    @Serializable(with = BigDecimalPriceSerializer::class)
    val price: BigDecimal,

    @property:GraphQLDescription("Detailed description of the product")
    val description: String,

    @property:GraphQLDescription("List of image URLs associated with the product")
    val images: List<String>,

    @property:GraphQLDescription("Material composition of the product")
    val material: String,

    @property:GraphQLDescription("Indicates whether the product is currently in stock")
    val inStock: Boolean,

    @property:GraphQLDescription("Country where the product was manufactured")
    val countryOfOrigin: String
)