package io.aoriani.ecomm.ui.screens.productdetails

import io.aoriani.ecomm.data.model.Product

interface ProductDetailsUiState {
    val title: String
    val imageUrl: String?

    data class Loading(
        override val title: String,
        override val imageUrl: String?
    ) : ProductDetailsUiState

    data class Loaded(val product: Product) : ProductDetailsUiState {
        override val title: String get() = product.name
        override val imageUrl: String get() = product.images.firstOrNull().orEmpty()
    }

    data class Error(
        override val title: String,
        override val imageUrl: String?
    ) : ProductDetailsUiState
}