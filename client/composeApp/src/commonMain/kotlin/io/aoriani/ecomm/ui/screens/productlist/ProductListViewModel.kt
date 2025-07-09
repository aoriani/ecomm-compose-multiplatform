package io.aoriani.ecomm.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import co.touchlab.kermit.Logger
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

private const val LOGTAG = "ProductListViewModel"

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val logger: Logger = Logger
) : ViewModel() {
    val state: StateFlow<ProductListUiState>
        field = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            state.update { ProductListUiState.Loading }
            productRepository.fetchProducts()
                .onSuccess { productPreviews ->
                    logger.i(tag = LOGTAG) { "Products fetched: $productPreviews" }
                    state.update { ProductListUiState.Success(productPreviews) }
                }
                .onFailure {
                    logger.e(throwable = it, tag = LOGTAG) { "Error fetching products" }
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
