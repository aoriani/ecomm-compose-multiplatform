package io.aoriani.ecomm.ui.screens.productdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.aoriani.ecomm.data.repositories.ProductRepository
import io.aoriani.ecomm.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class ProductDetailsViewModel(
    private val productRepository: ProductRepository, private val savedStateHandle: SavedStateHandle
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
            try {
                val product = productRepository.getProduct(route.id)
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
            } catch (ex: ProductRepository.GraphQlException) {
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