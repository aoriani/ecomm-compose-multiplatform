package io.aoriani.ecomm.ui.screens.productdetails

import androidx.core.bundle.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import androidx.savedstate.SavedStateRegistryOwner
import io.aoriani.ecomm.data.repositories.ProductRepository
import io.aoriani.ecomm.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val route: Routes.ProductDetails = savedStateHandle.toRoute()
    val state: StateFlow<ProductDetailsUiState>
        field = MutableStateFlow(ProductDetailsUiState.Loading(route.name))
    companion object {
        fun provideFactory(productRepository: ProductRepository, owner: SavedStateRegistryOwner,
                           defaultArgs: Bundle? = null,): AbstractSavedStateViewModelFactory {
            return object: AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: KClass<T>,
                    handle: SavedStateHandle
                ): T {
                    @Suppress("UNCHECKED_CAST")
                    return ProductDetailsViewModel(productRepository, handle) as T
                }
            }
        }
    }
}