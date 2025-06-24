package io.aoriani.ecomm.ui.screens.productdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.ui.screens.common.components.VerticalScrollBarIfSupported
import io.aoriani.ecomm.ui.screens.productdetails.components.ProductImage
import kotlinx.collections.immutable.persistentListOf
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
        Box(modifier = Modifier.padding(paddingValues)) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                ProductImage(
                    //TODO: Handle missing image better
                    imageUrl = state.imageUrl.orEmpty(),
                    contentDescription = state.title,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state is ProductDetailsUiState.Loaded) {
                    Text(text = state.product.name)
                    Text(text = state.product.description)
                }
            }
            VerticalScrollBarIfSupported(scrollState)
        }
    }
}


@Preview
@Composable
fun ProductListScreenPreview() {
    MaterialTheme {
        ProductDetailsScreen(
            state = ProductDetailsUiState.Loaded(
                product = Product(
                    id = "id",
                    name = "Product Name",
                    price = 10.0,
                    description = "Product Description",
                    images = persistentListOf("")
                )
            )
        )
    }
}