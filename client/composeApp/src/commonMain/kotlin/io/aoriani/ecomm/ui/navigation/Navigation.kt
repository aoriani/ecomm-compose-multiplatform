package io.aoriani.ecomm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.aoriani.ecomm.ui.screens.cart.cartScreen
import io.aoriani.ecomm.ui.screens.productdetails.productDetailsScreen
import io.aoriani.ecomm.ui.screens.productlist.productListScreen

/**
 * Sets up the navigation for the application using Jetpack Compose Navigation.
 *
 * This composable function defines the navigation graph, including all the screens
 * and their corresponding routes. It uses a [NavHostController] to manage navigation
 * and allows for an optional callback [onNavHostReady] to be invoked when the
 * NavHost is ready.
 *
 * The navigation graph includes the following screens:
 * - Product List Screen: The initial screen displaying a list of products.
 * - Product Details Screen: Displays detailed information about a selected product.
 * - Cart Screen: Shows the items currently in the user's shopping cart.
 *
 * @param navController The [NavHostController] to be used for navigation. Defaults to a new controller created by [rememberNavController].
 * @param onNavHostReady A suspend function that will be called with the [NavController] once the NavHost is initialized and ready. This can be used for setup tasks that depend on the NavController.
 */
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    NavHost(navController = navController, startDestination = Routes.ProductList) {
        with(navController) {
            productListScreen()
            productDetailsScreen()
            cartScreen()
        }
    }

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }
}
