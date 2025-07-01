package io.aoriani.ecomm.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                val list = productRepository.fetchProducts()
                Logger.i("ProductListViewModel") { "Products fetched: $list" }
                state.update { ProductListUiState.Success(list) }
            } catch (ex: ProductRepository.GraphQlException) {
                Logger.e(ex, "ProductListViewModel") { "Error fetching products" }
                state.update { ProductListUiState.Error }
            }
        }
    }
}