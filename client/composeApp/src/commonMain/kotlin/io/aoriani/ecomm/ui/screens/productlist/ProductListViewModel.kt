package io.aoriani.ecomm.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import co.touchlab.kermit.Logger
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.domain.AddToCartUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

private const val LOGTAG = "ProductListViewModel"

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val logger: Logger = Logger,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    val state: StateFlow<ProductListUiState>
        field = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch(dispatcher) {
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
}
