package io.aoriani.ecomm.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Represents the different screens/routes in the application.
 * This sealed interface is used for type-safe navigation and serialization
 * with Kotlinx Serialization for passing arguments between destinations.
 */
sealed interface Routes {
    /**
     * Represents the product list screen.
     * This is the initial screen of the application.
     */
    @Serializable
    object ProductList : Routes

    /**
     * Represents the route for displaying the details of a specific product.
     *
     * @property id The unique identifier of the product.
     * @property name The name of the product.
     * @property imageUrl The URL of the product's image, if available.
     */
    @Serializable
    data class ProductDetails(val id: String, val name: String, val imageUrl: String?) : Routes

    /**
     * Represents the shopping cart screen.
     */
    @Serializable
    object Cart : Routes
}