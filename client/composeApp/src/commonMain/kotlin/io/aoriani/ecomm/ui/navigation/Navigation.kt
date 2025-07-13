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
