package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes
import org.koin.compose.koinInject

internal fun NavGraphBuilder.productListScreen(navController: NavHostController) {
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
}