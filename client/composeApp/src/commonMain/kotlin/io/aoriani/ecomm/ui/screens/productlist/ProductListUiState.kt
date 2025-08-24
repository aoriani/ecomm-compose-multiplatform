package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList

/**
 * Represents the different states of the Product List UI.
 *
 * This sealed interface defines the possible states the product list screen can be in:
 * - [Loading]: Indicates that the product list is currently being fetched.
 * - [Error]: Indicates that an error occurred while fetching the product list. It provides a way to retry the operation.
 * - [Success]: Indicates that the product list was fetched successfully and contains the list of products. It also provides a way to add a product to the cart.
 */
sealed interface ProductListUiState {
    val cartItemCount: Int

    /**
     * Represents the loading state of the ProductList screen.
     * This state is active when the product list is being fetched or refreshed.
     */
    data class Loading(override val cartItemCount: Int = 0) : ProductListUiState
    /**
     * Represents the error state of the product list screen.
     * This state is used when there's an issue fetching or displaying the product list.
     *
     * @property _reload A lambda function that can be invoked to retry loading the product list.
     */
    class Error(override val cartItemCount: Int, private val _reload: () -> Unit) : ProductListUiState {
        /**
         * Reloads the product list.
         *
         * This function is typically called when an error occurs during the initial loading of the product list,
         * allowing the user to attempt to reload the data.
         */
        fun reload() = _reload()
    }

    /**
     * Represents the successful state of the product list screen.
     *
     * @param products The immutable list of product previews to display.
     * @param _addToCart A lambda function to handle adding a product to the cart. This is private
     *                   and exposed via the public `addToCart` method.
     */
    data class Success(
        val products: ImmutableList<ProductPreview>,
        override val cartItemCount: Int,
        private val _addToCart: (ProductBasic) -> Unit
    ) : ProductListUiState {
        fun addToCart(product: ProductBasic) = _addToCart(product)
    }
}