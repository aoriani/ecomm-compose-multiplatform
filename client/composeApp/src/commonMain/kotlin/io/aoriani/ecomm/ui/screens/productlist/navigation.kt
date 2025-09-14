package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes
import org.koin.compose.viewmodel.koinViewModel

/**
 * Defines the product list screen within the navigation graph.
 *
 * This extension function configures a composable route for the `Routes.ProductList` destination.
 * It initializes the [ProductListViewModel] using Koin, collects its state, and then
 * renders the [ProductListScreen] composable.
 *
 * The [ProductListScreen] is provided with:
 * - The current state from the ViewModel.
 * - A lambda function to navigate to the shopping cart screen (`Routes.Cart`).
 * - A lambda function to navigate to the product details screen (`Routes.ProductDetails`),
 *   passing the selected product's ID, name, and image URL.
 *
 * This function operates within the context of a [NavHostController] to enable navigation.
 */
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