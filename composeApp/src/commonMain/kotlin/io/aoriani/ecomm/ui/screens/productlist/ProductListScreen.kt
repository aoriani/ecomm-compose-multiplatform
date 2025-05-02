package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import io.aoriani.ecomm.ui.screens.productdetails.ProductDetailsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductListScreen(navigateToCart: () -> Unit = {}) {
        LoadingOverlay(false) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Hello") },
                        actions = {
                            Button(onClick = navigateToCart) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        }
                    )
                }
            ) { paddingValues ->
            }
        }
}

@Preview
@Composable
fun ProductListScreenPreview() {
    MaterialTheme {
        ProductDetailsScreen()
    }
}