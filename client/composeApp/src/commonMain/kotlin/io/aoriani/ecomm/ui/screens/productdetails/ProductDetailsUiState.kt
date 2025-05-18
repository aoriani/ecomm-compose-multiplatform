package io.aoriani.ecomm.ui.screens.productdetails

interface ProductDetailsUiState {
    val title: String
    val imageUrl: String

    data class Loading(
        override val title: String,
        override val imageUrl: String
    ) : ProductDetailsUiState
}