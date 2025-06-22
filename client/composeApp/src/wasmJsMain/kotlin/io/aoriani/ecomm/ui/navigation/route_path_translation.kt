package io.aoriani.ecomm.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute

internal fun getBackStackEntryRoute(entry: NavBackStackEntry): String {
    val route = entry.destination.route.orEmpty()
    return when {
        route.startsWith(Routes.ProductList.serializer().descriptor.serialName) -> {
            "#start"
        }

        route.startsWith(Routes.ProductDetails.serializer().descriptor.serialName) -> {
            val args = entry.toRoute<Routes.ProductDetails>()
            "#details_${args.id}"
        }

        route.startsWith(Routes.Cart.serializer().descriptor.serialName) -> {
            "#cart"
        }

        else -> ""
    }
}