package io.aoriani.ecomm.ui.screens.cart

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes

internal fun NavGraphBuilder.cartScreen(navController: NavHostController) {
    composable<Routes.Cart> {
        CartScreen(navigateBack = { navController.popBackStack() })
    }
}