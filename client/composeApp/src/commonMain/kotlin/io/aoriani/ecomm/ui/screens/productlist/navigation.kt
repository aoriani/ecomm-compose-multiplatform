package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes
import org.koin.compose.koinInject

context(navController: NavHostController)
internal fun NavGraphBuilder.productListScreen() {
    composable<Routes.ProductList> {
        val viewModel: ProductListViewModel = viewModel(
            factory = ProductListViewModel.Companion.Factory(
                productRepository = koinInject(),
                addToCartUseCase = koinInject()
            )
        )
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