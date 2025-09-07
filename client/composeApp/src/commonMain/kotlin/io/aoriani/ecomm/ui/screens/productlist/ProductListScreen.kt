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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.content_description_cart
import ecommerceapp.composeapp.generated.resources.product_list_snackbar_action_retry
import ecommerceapp.composeapp.generated.resources.product_list_snackbar_generic_error
import ecommerceapp.composeapp.generated.resources.product_list_title
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.ui.screens.common.components.VerticalScrollbarIfSupported
import io.aoriani.ecomm.ui.screens.productlist.components.LoadingOverlay
import io.aoriani.ecomm.ui.screens.productlist.components.ProductTile
import io.aoriani.ecomm.ui.test.TestTags
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    state: ProductListUiState,
    navigateToCart: () -> Unit = {},
    navigateToProductDetails: (ProductBasic) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LoadingOverlay(false) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.product_list_title)) },
                    actions = {
                        BadgedBox(badge = {
                            if (state.cartItemCount > 0) {
                                Badge(modifier = Modifier.testTag(TestTags.screens.productlist.cartCountBagde)) {
                                    Text(
                                        state.cartItemCount.toString()
                                    )
                                }
                            }
                        }) {
                            IconButton(onClick = navigateToCart) {
                                Icon(
                                    Icons.Filled.ShoppingCart,
                                    contentDescription = stringResource(Res.string.content_description_cart)
                                )
                            }
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { paddingValues ->
            LoadingOverlay(
                isLoading = state is ProductListUiState.Loading,
                modifier = Modifier.padding(paddingValues)
            ) {
                when (state) {
                    is ProductListUiState.Success -> {
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
                                        onTileClicked = { navigateToProductDetails(item) },
                                        onAddToCartClicked = { state.addToCart(item) }
                                    )
                                }
                            }
                            VerticalScrollbarIfSupported(scrollState)
                        }
                    }

                    is ProductListUiState.Error -> {
                        val scope = rememberCoroutineScope()
                        val message = stringResource(Res.string.product_list_snackbar_generic_error)
                        val actionLabel =
                            stringResource(Res.string.product_list_snackbar_action_retry)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = actionLabel
                            )

                            when (result) {
                                SnackbarResult.Dismissed -> Unit
                                SnackbarResult.ActionPerformed -> {
                                    state.reload()
                                }
                            }
                        }
                    }

                    is ProductListUiState.Loading -> Unit
                }
            }
        }
    }
}

private class ProductListUiStateParameterProvider : PreviewParameterProvider<ProductListUiState> {
    override val values = sequenceOf(
        ProductListUiState.Loading(),
        ProductListUiState.Error(),
        ProductListUiState.Success(),
        ProductListUiState.Success(
            cartItemCount = 4, products = Array(20) { index ->
                ProductPreview(
                    id = ProductBasic.Id("id"),
                    name = "Product $index",
                    price = DollarAmount("10.0"),
                    thumbnailUrl = "https://picsum.photos/200/300"
                )
            }.toPersistentList()
        )
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview(@PreviewParameter(ProductListUiStateParameterProvider::class) state: ProductListUiState) {
    MaterialExpressiveTheme {
        ProductListScreen(state = state)
    }
}