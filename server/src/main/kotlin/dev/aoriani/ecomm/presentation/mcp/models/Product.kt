package dev.aoriani.ecomm.presentation.mcp.models

import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.meta.Title
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import dev.aoriani.ecomm.domain.models.Product as DomainProduct

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
@Serializable
@Title("Product")
@Description("Represents a product available in the e-commerce catalog.")
data class Product(

    @Description("Unique identifier for the product.")
    val id: String,

    @Description("Name of the product.")
    val name: String,

    @Serializable(with = BigDecimalPriceSerializer::class)
    @Description("Unit price of the product.")
    val price: BigDecimal,

    @Description("Detailed text description of the product.")
    val description: String,

    @Description("Array of image URLs for the product.")
    val images: List<String>,

    @Description("Material composition of the product.")
    val material: String,

    @Description("Indicates whether the product is currently in stock.")
    val inStock: Boolean,

    @Description("Country where the product was manufactured.")
    val countryOfOrigin: String
)

@Serializable
@Title("Products")
@Description("Represents the list of product available in the e-commerce catalog.")
data class Products(
    @Description("List of products.")
    val products: List<Product>
)

@Serializable
@Title("GetProductByIdRequest")
@Description("Request to get a product by its unique identifier.")
data class GetProductByIdRequest(
    @Description("The unique identifier of the product to retrieve.")
    val id: String
)

fun DomainProduct.toMcpProduct(): Product = Product(
    id = this.id.id,
    name = this.name,
    price = this.price,
    description = this.description,
    images = this.images,
    material = this.material,
    inStock = this.inStock,
    countryOfOrigin = this.countryOfOrigin
)

