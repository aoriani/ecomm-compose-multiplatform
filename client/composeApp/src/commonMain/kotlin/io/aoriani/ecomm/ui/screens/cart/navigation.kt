package io.aoriani.ecomm.ui.screens.cart

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes

context(navController: NavHostController)
internal fun NavGraphBuilder.cartScreen() {
    composable<Routes.Cart> {
        CartScreen(navigateBack = { navController.popBackStack() })
    }
}