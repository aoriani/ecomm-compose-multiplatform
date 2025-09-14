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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val LOGTAG = "ProductListViewModel"

/**
 * ViewModel for the Product List screen.
 *
 * This ViewModel is responsible for fetching the list of products, managing the UI state,
 * and handling user interactions such as adding a product to the cart.
 *
 * @property productRepository The repository for fetching product data.
 * @property cartRepository The repository for managing the shopping cart.
 * @property addToCartUseCase The use case for adding a product to the cart.
 * @property logger The logger for logging events.
 * @property dispatcher The coroutine dispatcher for executing background tasks.
 */
class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val logger: Logger = Logger,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    /**
     * The state of the Product List screen.
     */
    val state: StateFlow<ProductListUiState>
        field = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading())

    init {
        fetchProducts()
        viewModelScope.launch(dispatcher) {
            cartRepository.state.collect { cartState ->
                state.update { it.copyWithNewCartItemCount(cartState.count) }
            }
        }
    }

    /**
     * Fetches the list of products from the repository and updates the UI state.
     */
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

    /**
     * Adds a product to the cart.
     *
     * @param product The product to add to the cart.
     */
    fun addToCart(product: ProductBasic) {
        viewModelScope.launch(dispatcher) {
            logger.i(tag = LOGTAG) { "Adding to cart: $product" }
            addToCartUseCase(product)
        }
    }
}
