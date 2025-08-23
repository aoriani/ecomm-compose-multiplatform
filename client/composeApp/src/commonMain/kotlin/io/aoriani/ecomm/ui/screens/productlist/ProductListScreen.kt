package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.content_description_cart
import ecommerceapp.composeapp.generated.resources.product_list_title
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.ui.screens.common.components.VerticalScrollbarIfSupported
import io.aoriani.ecomm.ui.screens.productlist.components.LoadingOverlay
import io.aoriani.ecomm.ui.screens.productlist.components.ProductTile
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    state: ProductListUiState,
    navigateToCart: () -> Unit = {},
    navigateToProductDetails: (ProductPreview) -> Unit = {}
) {
    LoadingOverlay(false) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.product_list_title)) },
                    actions = {
                        IconButton(onClick = navigateToCart) {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = stringResource(Res.string.content_description_cart)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LoadingOverlay(
                isLoading = state is ProductListUiState.Loading,
                modifier = Modifier.padding(paddingValues)
            ) {
                if (state is ProductListUiState.Success) {
                    val scrollState = rememberLazyGridState()
                    Box {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            state = scrollState,
                            modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE))
                        ) {
                            items(state.products) { item ->
                                ProductTile(
                                    product = item,
                                    onClick = { navigateToProductDetails(item) })
                            }
                        }
                        VerticalScrollbarIfSupported(scrollState)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(group = "Product List", name = "Loading", showBackground = true)
@Composable
fun ProductListScreenPreview() {
    MaterialExpressiveTheme {
        ProductListScreen(state = ProductListUiState.Loading)
    }
}