package io.aoriani.ecomm.ui.screens.productdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.domain.AddToCartUseCase
import io.aoriani.ecomm.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productDetailsRoute: Routes.ProductDetails = savedStateHandle.toRoute()
    val state: StateFlow<ProductDetailsUiState>
        field = MutableStateFlow<ProductDetailsUiState>(
            ProductDetailsUiState.Loading(
                title = productDetailsRoute.name, imageUrl = productDetailsRoute.imageUrl
            )
        )

    init {
        fetchProductDetails()
    }

    fun fetchProductDetails() {
        state.update {
            ProductDetailsUiState.Loading(
                title = productDetailsRoute.name, imageUrl = productDetailsRoute.imageUrl
            )
        }
        viewModelScope.launch {
            productRepository.getProduct(productDetailsRoute.id)
                .onSuccess { product -> // product is of type Product?
                    if (product == null) {
                        state.update {
                            ProductDetailsUiState.Error(
                                title = productDetailsRoute.name,
                                imageUrl = productDetailsRoute.imageUrl,
                                _retry = ::fetchProductDetails
                            )
                        }
                    } else {
                        state.update {
                            ProductDetailsUiState.Loaded(
                                product = product,
                                _addToCart = ::addToCart
                            )
                        }
                    }
                }
                .onFailure {
                    // Optionally, log the error 'it' using a Logger
                    state.update {
                        ProductDetailsUiState.Error(
                            title = productDetailsRoute.name,
                            imageUrl = productDetailsRoute.imageUrl,
                            _retry = ::fetchProductDetails
                        )
                    }
                }
        }
    }

    fun addToCart() {
        val currentUiState = state.value
        check(currentUiState is ProductDetailsUiState.Loaded) {
            "Cannot add to cart from state $currentUiState"
        }
        viewModelScope.launch {
            addToCartUseCase(currentUiState.product)
        }
    }
}