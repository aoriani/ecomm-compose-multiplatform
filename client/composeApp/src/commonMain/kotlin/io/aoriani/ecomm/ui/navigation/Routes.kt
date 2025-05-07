package io.aoriani.ecomm.ui.navigation

import io.aoriani.ecomm.data.model.Product
import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    object ProductList: Routes

    @Serializable
    data class ProductDetails(val product: String): Routes

    @Serializable
    object Cart: Routes
}