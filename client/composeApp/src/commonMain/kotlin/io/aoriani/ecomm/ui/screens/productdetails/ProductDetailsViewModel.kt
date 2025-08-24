package io.aoriani.ecomm.ui.screens.productdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.domain.AddToCartUseCase
import io.aoriani.ecomm.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route: Routes.ProductDetails = savedStateHandle.toRoute()
    val state: StateFlow<ProductDetailsUiState>
        field = MutableStateFlow<ProductDetailsUiState>(
            ProductDetailsUiState.Loading(
                title = route.name, imageUrl = route.imageUrl
            )
        )

    init {
        fetchProductDetails()
    }

    private fun fetchProductDetails() {
        viewModelScope.launch {
            productRepository.getProduct(route.id)
                .onSuccess { product -> // product is of type Product?
                    if (product == null) {
                        state.update {
                            ProductDetailsUiState.Error(
                                title = route.name,
                                imageUrl = route.imageUrl
                            )
                        }
                    } else {
                        state.update { ProductDetailsUiState.Loaded(product) }
                    }
                }
                .onFailure {
                    // Optionally, log the error 'it' using a Logger
                    state.update {
                        ProductDetailsUiState.Error(
                            title = route.name,
                            imageUrl = route.imageUrl
                        )
                    }
                }
        }
    }
}