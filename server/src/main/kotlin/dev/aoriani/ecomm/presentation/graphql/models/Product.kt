package dev.aoriani.ecomm.presentation.graphql.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import dev.aoriani.ecomm.domain.models.Product as DomainProduct
import java.math.BigDecimal

@GraphQLDescription("Represents a product available in the e-commerce catalog")
data class Product(
    @property:GraphQLDescription("Unique identifier of the product")
    val id: ID,

    @property:GraphQLDescription("Name of the product")
    val name: String,

    @property:GraphQLDescription("Price of the product in USD")
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

fun DomainProduct.toGraphQlProduct(): Product = Product(
    id = ID(this.id),
    name = this.name,
    price = this.price,
    description = this.description,
    images = this.images,
    material = this.material,
    inStock = this.inStock,
    countryOfOrigin = this.countryOfOrigin
)
