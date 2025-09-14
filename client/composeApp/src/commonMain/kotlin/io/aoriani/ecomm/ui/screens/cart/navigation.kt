package io.aoriani.ecomm.ui.screens.cart

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes

/**
 * Defines the cart screen in the navigation graph.
 *
 * This function is an extension on `NavGraphBuilder` and uses a `NavHostController`
 * as a context receiver to manage navigation.
 * It sets up a composable route for `Routes.Cart`. When this route is navigated to,
 * the `CartScreen` composable is displayed.
 *
 * The `CartScreen` is provided with a `navigateBack` lambda that pops the back stack,
 * allowing the user to return to the previous screen.
 */
context(navController: NavHostController)
internal fun NavGraphBuilder.cartScreen() {
    composable<Routes.Cart> {
        CartScreen(navigateBack = { navController.popBackStack() })
    }
}
