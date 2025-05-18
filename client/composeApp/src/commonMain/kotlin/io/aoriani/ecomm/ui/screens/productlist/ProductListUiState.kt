package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.data.model.ProductPreview
import kotlinx.collections.immutable.ImmutableList

sealed interface ProductListUiState {
    object Loading : ProductListUiState
    object Error : ProductListUiState
    data class Success(val products: ImmutableList<ProductPreview>) : ProductListUiState
}