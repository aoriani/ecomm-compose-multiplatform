package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents the different states of the Product List UI.
 *
 * This sealed interface defines the possible states the product list screen can be in:
 * - [Loading]: Indicates that the product list is currently being fetched.
 * - [Error]: Indicates that an error occurred while fetching the product list. It provides a way to retry the operation.
 * - [Success]: Indicates that the product list was fetched successfully and contains the list of products. It also provides a way to add a product to the cart.
 */
sealed interface ProductListUiState {
    /**
     * The number of items currently in the shopping cart.
     * This property is common to all UI states and is used to display a badge or indicator
     * of the cart's content.
     */
    val cartItemCount: Int

    /**
     * Creates a new instance of the current `ProductListUiState` with an updated `cartItemCount`.
     *
     * This function is useful when the number of items in the cart changes, and the UI state
     * needs to reflect this change without altering other aspects of the state (like the list
     * of products in `Success` state, or the retry action in `Error` state).
     *
     * @param newItemCount The new number of items in the cart.
     * @return A new `ProductListUiState` instance with the updated `cartItemCount`.
     */
    fun copyWithNewCartItemCount(newItemCount: Int): ProductListUiState

    /**
     * Represents the loading state of the ProductList screen.
     * This state is active when the product list is being fetched or refreshed.
     */
    data class Loading(override val cartItemCount: Int = 0) : ProductListUiState {
        override fun copyWithNewCartItemCount(newItemCount: Int): ProductListUiState {
            return copy(cartItemCount = newItemCount)
        }
    }

    /**
     * Represents the error state of the product list screen.
     * This state is used when there's an issue fetching or displaying the product list.
     *
     * @property _reload A lambda function that can be invoked to retry loading the product list.
     */
    data class Error(override val cartItemCount: Int = 0, private val _reload: () -> Unit = {}) : ProductListUiState {
        /**
         * Reloads the product list.
         *
         * This function is typically called when an error occurs during the initial loading of the product list,
         * allowing the user to attempt to reload the data.
         */
        fun reload() = _reload()

        override fun copyWithNewCartItemCount(newItemCount: Int): ProductListUiState {
            return copy(cartItemCount = newItemCount)
        }
    }

    /**
     * Represents the successful state of the product list screen.
     *
     * @param products The immutable list of product previews to display.
     * @param _addToCart A lambda function to handle adding a product to the cart. This is private
     *                   and exposed via the public `addToCart` method.
     */
    data class Success(
        val products: ImmutableList<ProductPreview> = persistentListOf(),
        override val cartItemCount: Int = 0,
        private val _addToCart: (ProductBasic) -> Unit = {}
    ) : ProductListUiState {
        /**
         * Adds the given product to the cart.
         *
         * This function is invoked when the user interacts with a product in the list
         * to add it to their shopping cart.
         *
         * @param product The product to be added to the cart.
         */
        fun addToCart(product: ProductBasic) = _addToCart(product)

        override fun copyWithNewCartItemCount(newItemCount: Int): ProductListUiState {
            return copy(cartItemCount = newItemCount)
        }
    }
}
