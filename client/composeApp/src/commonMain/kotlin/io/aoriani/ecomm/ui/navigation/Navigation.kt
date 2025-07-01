package io.aoriani.ecomm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.aoriani.ecomm.ui.screens.cart.CartScreen
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsScreen
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsUiState
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsViewModel
import io.aoriani.ecomm.ui.screens.productlist.ProductListScreen
import io.aoriani.ecomm.ui.screens.productlist.ProductListViewModel
import org.koin.compose.koinInject

@Composable
fun Navigation(
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.ProductList) {
        composable<Routes.ProductList> {
            val viewModel: ProductListViewModel = viewModel(
                factory = ProductListViewModel.Companion.Factory(
                    koinInject()
                )
            )
            val state by viewModel.state.collectAsStateWithLifecycle()
            ProductListScreen(
                state = state,
                navigateToCart = { navController.navigate(Routes.Cart) },
                navigateToProductDetails = { product ->
                    navController.navigate(
                        Routes.ProductDetails(
                            id = product.id,
                            name = product.name,
                            imageUrl = product.thumbnailUrl
                        )
                    )
                }
            )
        }

        composable<Routes.ProductDetails> { backStackEntry ->
            val viewModel: ProductDetailsViewModel = viewModel(
                factory = ProductDetailsViewModel.Companion.Factory(
                    koinInject(),
                )
            )
            val state: ProductDetailsUiState by viewModel.state.collectAsStateWithLifecycle()
            ProductDetailsScreen(state, navigateBack = { navController.popBackStack() })
        }

        composable<Routes.Cart> {
            CartScreen(navigateBack = { navController.popBackStack() })
        }
    }

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

}