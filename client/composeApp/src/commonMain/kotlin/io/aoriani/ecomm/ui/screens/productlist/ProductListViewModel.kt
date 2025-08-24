package io.aoriani.ecomm.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.domain.AddToCartUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun fetchProducts() {
        viewModelScope.launch(dispatcher) {
            state.update { ProductListUiState.Loading }
            productRepository.fetchProducts()
                .onSuccess { productPreviews ->
                    logger.i(tag = LOGTAG) { "Products fetched: $productPreviews" }
                    state.update { ProductListUiState.Success(
                        products = productPreviews,
                        _addToCart = ::addToCart
                    ) }
                }
                .onFailure {
                    logger.e(throwable = it, tag = LOGTAG) { "Error fetching products" }
                    state.update { ProductListUiState.Error(_reload = ::fetchProducts) }
                }
        }
    }

    fun addToCart(product: ProductBasic) {
        viewModelScope.launch(dispatcher) {
            logger.i(tag = LOGTAG) { "Adding to cart: $product" }
            addToCartUseCase(product)
        }
    }
}
