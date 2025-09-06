package io.aoriani.ecomm.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.repositories.cart.CartRepository
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import io.aoriani.ecomm.domain.AddToCartUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val LOGTAG = "ProductListViewModel"

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val logger: Logger = Logger,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    val state: StateFlow<ProductListUiState>
        field = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading())

    private val cartItemCount = cartRepository.state.map { cartState -> cartState.count }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        fetchProducts()
        viewModelScope.launch(dispatcher) {
            cartItemCount.collect { count ->
                state.update { it.copyWithNewCartItemCount(count) }
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch(dispatcher) {
            state.update { currentState -> ProductListUiState.Loading(cartItemCount = currentState.cartItemCount) }
            productRepository.fetchProducts()
                .onSuccess { productPreviews ->
                    logger.i(tag = LOGTAG) { "Products fetched: $productPreviews" }
                    state.update { currentState->
                        ProductListUiState.Success(
                            products = productPreviews,
                            cartItemCount = currentState.cartItemCount,
                            _addToCart = ::addToCart
                        )
                    }
                }
                .onFailure {
                    logger.e(throwable = it, tag = LOGTAG) { "Error fetching products" }
                    state.update { currentState ->
                        ProductListUiState.Error(
                            cartItemCount = currentState.cartItemCount,
                            _reload = ::fetchProducts
                        )
                    }
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
