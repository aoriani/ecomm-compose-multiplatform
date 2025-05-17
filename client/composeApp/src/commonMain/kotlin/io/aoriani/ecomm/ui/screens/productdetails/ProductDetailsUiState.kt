package io.aoriani.ecomm.ui.screens.productdetails

interface ProductDetailsUiState {
    val title: String
    data class Loading(override val title: String): ProductDetailsUiState
}