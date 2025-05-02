package io.aoriani.ecomm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.aoriani.ecomm.ui.screens.cart.CartScreen
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsScreen
import io.aoriani.ecomm.ui.screens.productlist.ProductListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.ProductList) {
        composable<Routes.ProductList> {
            ProductListScreen()
        }

        composable<Routes.ProductDetails> {
            ProductDetailsScreen()
        }

        composable<Routes.Cart> {
            CartScreen()
        }
    }

}