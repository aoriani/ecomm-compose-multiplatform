package io.aoriani.ecomm.ui.screens.productdetails

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes
import org.koin.compose.viewmodel.koinViewModel

/**
 * Defines the navigation for the product details screen.
 *
 * This function is an extension of [NavGraphBuilder] and uses a [NavHostController] context
 * to handle navigation. It sets up a composable route for [Routes.ProductDetails].
 *
 * When this route is navigated to, it:
 *  1. Obtains an instance of [ProductDetailsViewModel] using Koin.
 *  2. Collects the UI state ([ProductDetailsUiState]) from the ViewModel as a Compose state.
 *  3. Displays the [ProductDetailsScreen] composable, passing the current UI state and a
 *     lambda function to navigate back (`navController.popBackStack()`).
 */
context(navController: NavHostController)
internal fun NavGraphBuilder.productDetailsScreen() {
    composable<Routes.ProductDetails> { backStackEntry ->
        val viewModel: ProductDetailsViewModel = koinViewModel()
        val state: ProductDetailsUiState by viewModel.state.collectAsStateWithLifecycle()
        ProductDetailsScreen(state, navigateBack = { navController.popBackStack() })
    }
}
