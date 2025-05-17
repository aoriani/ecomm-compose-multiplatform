package io.aoriani.ecomm.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    object ProductList: Routes

    @Serializable
    data class ProductDetails(val id: String, val name: String): Routes

    @Serializable
    object Cart: Routes
}