package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes
import org.koin.compose.viewmodel.koinViewModel

context(navController: NavHostController)
internal fun NavGraphBuilder.productListScreen() {
    composable<Routes.ProductList> {
        val viewModel: ProductListViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        ProductListScreen(
            state = state,
            navigateToCart = { navController.navigate(Routes.Cart) },
            navigateToProductDetails = { product ->
                navController.navigate(
                    Routes.ProductDetails(
                        id = product.id.value,
                        name = product.name,
                        imageUrl = product.thumbnailUrl
                    )
                )
            }
        )
    }
}