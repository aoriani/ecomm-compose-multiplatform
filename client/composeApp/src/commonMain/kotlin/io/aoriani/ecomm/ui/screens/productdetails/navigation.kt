package io.aoriani.ecomm.ui.screens.productdetails

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.aoriani.ecomm.ui.navigation.Routes
import org.koin.compose.koinInject

context(navController: NavHostController)
internal fun NavGraphBuilder.productDetailsScreen() {
    composable<Routes.ProductDetails> { backStackEntry ->
        val viewModel: ProductDetailsViewModel = viewModel(
            factory = ProductDetailsViewModel.Companion.Factory(
                koinInject(),
            )
        )
        val state: ProductDetailsUiState by viewModel.state.collectAsStateWithLifecycle()
        ProductDetailsScreen(state, navigateBack = { navController.popBackStack() })
    }
}
