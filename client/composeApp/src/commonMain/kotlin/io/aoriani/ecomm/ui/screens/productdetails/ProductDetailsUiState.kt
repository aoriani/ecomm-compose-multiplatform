package io.aoriani.ecomm.ui.screens.productdetails

import io.aoriani.ecomm.data.model.Product

/**
 * Represents the various states of the Product Details screen.
 * This sealed interface ensures that all possible states are handled explicitly,
 * making the UI state management more robust and predictable.
 */
sealed interface ProductDetailsUiState {
    /**
     * The title to be displayed on the screen's app bar.
     * This value changes depending on the current state (e.g., "Loading...", product name, or "Error").
     */
    val title: String

    /**
     * The URL of the product image to be displayed.
     * This can be null if there is no image available or during the initial loading phase.
     */
    val imageUrl: String?

    /**
     * Represents the loading state of the Product Details screen.
     * This state is active while the product data is being fetched from the repository.
     *
     * @property title The title to be displayed while loading, typically "Loading...".
     * @property imageUrl A placeholder or cached image URL to be displayed during loading.
     */
    data class Loading(
        override val title: String,
        override val imageUrl: String?
    ) : ProductDetailsUiState

    /**
     * Represents the success state of the Product Details screen, where the product data has been successfully loaded.
     *
     * @property product The fully loaded [Product] object to be displayed.
     */
    data class Loaded(val product: Product) : ProductDetailsUiState {
        /**
         * The title of the screen, derived from the product's name.
         */
        override val title: String get() = product.name

        /**
         * The primary image URL for the product, taken from the first image in the product's image list.
         * Returns an empty string if the product has no images.
         */
        override val imageUrl: String get() = product.images.firstOrNull().orEmpty()
    }

    /**
     * Represents the error state of the Product Details screen.
     * This state is triggered if fetching the product data fails.
     *
     * @property title The title to be displayed in case of an error, typically "Error".
     * @property imageUrl An optional image URL to display, which could be a placeholder error image.
     */
    data class Error(
        override val title: String,
        override val imageUrl: String?
    ) : ProductDetailsUiState
}
