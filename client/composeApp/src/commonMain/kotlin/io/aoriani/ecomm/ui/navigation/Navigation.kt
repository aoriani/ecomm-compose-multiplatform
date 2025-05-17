package io.aoriani.ecomm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.aoriani.ecomm.di.Deps
import io.aoriani.ecomm.ui.screens.cart.CartScreen
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsScreen
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsUiState
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsViewModel
import io.aoriani.ecomm.ui.screens.productlist.ProductListScreen
import io.aoriani.ecomm.ui.screens.productlist.ProductListViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.ProductList) {
        composable<Routes.ProductList> {
            val viewModel: ProductListViewModel = viewModel(
                factory = ProductListViewModel.Companion.Factory(
                    Deps.productRepository
                )
            )
            val state by viewModel.state.collectAsState()
            ProductListScreen(
                state = state,
                navigateToCart = { navController.navigate(Routes.Cart) },
                navigateToProductDetails = { product ->
                    navController.navigate(
                        Routes.ProductDetails(
                            id = product.id,
                            name = product.name
                        )
                    )
                }
            )
        }

        composable<Routes.ProductDetails> { backStackEntry ->
            val title = backStackEntry.toRoute<Routes.ProductDetails>().name
            val viewModel: ProductDetailsViewModel = viewModel(
                factory = ProductDetailsViewModel.provideFactory(
                    Deps.productRepository,
                    backStackEntry
                )
            )
            val state: ProductDetailsUiState by viewModel.state.collectAsState()
            ProductDetailsScreen(state, navigateBack = { navController.popBackStack() })
        }

        composable<Routes.Cart> {
            CartScreen(navigateBack = { navController.popBackStack() })
        }
    }

}