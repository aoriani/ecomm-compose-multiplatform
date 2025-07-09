package io.aoriani.ecomm.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import co.touchlab.kermit.Logger
import io.aoriani.ecomm.data.repositories.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class ProductListViewModel(private val productRepository: ProductRepository) : ViewModel() {
    val state: StateFlow<ProductListUiState>
        field = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                state.update { ProductListUiState.Loading }
                val products = productRepository.fetchProducts()
                Logger.i("ProductListViewModel") { "Products fetched: $products" }
                state.update { ProductListUiState.Success(products) }
            } catch (graphQlException: ProductRepository.GraphQlException) {
                Logger.e(graphQlException, "ProductListViewModel") { "Error fetching products" }
                state.update { ProductListUiState.Error }
            }
        }
    }

    companion object {
        class Factory(private val productRepository: ProductRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: KClass<T>,
                extras: CreationExtras
            ): T {
                @Suppress("UNCHECKED_CAST")
                return ProductListViewModel(productRepository) as T
            }
        }
    }
}