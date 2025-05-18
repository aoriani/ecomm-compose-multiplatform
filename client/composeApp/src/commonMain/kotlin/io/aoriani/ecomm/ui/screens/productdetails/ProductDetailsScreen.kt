package io.aoriani.ecomm.ui.screens.productdetails

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.content_description_back
import io.aoriani.ecomm.ui.screens.productdetails.components.ProductImage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsScreen(
    state: ProductDetailsUiState,
    navigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.title) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                Res.string.content_description_back
                            )
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        ProductImage(
            state.imageUrl,
            state.title,
            modifier = Modifier.padding(paddingValues).fillMaxWidth()
        )
    }
}


@Preview
@Composable
fun ProductListScreenPreview() {
    MaterialTheme {
        ProductDetailsScreen(
            state = ProductDetailsUiState.Loading(
                title = "Product Name",
                imageUrl = ""
            )
        )
    }
}