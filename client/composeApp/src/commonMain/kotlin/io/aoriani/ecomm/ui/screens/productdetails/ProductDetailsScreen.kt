package io.aoriani.ecomm.ui.screens.productdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.content_description_back
import ecommerceapp.composeapp.generated.resources.product_detail_add_to_cart_fab_content_description
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.ui.screens.common.components.VerticalScrollbarIfSupported
import io.aoriani.ecomm.ui.screens.productdetails.components.ProductImage
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
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
        },
        floatingActionButton = {
            if (state is ProductDetailsUiState.Loaded) {
                FloatingActionButton(onClick = { state.addToCart() }) {
                    Icon(
                        Icons.Default.AddShoppingCart,
                        contentDescription = stringResource(
                            Res.string.product_detail_add_to_cart_fab_content_description
                        )
                    )
                }
            }
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
            VerticalScrollbarIfSupported(scrollState)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun ProductDetailsScreenPreview() {
    MaterialExpressiveTheme {
        ProductDetailsScreen(
            state = ProductDetailsUiState.Loaded(
                product = Product(
                    id = ProductBasic.Id("id"),
                    name = "Product Name",
                    price = DollarAmount("10.0"),
                    description = "Product Description",
                    images = persistentListOf(""),
                    material = "Material",
                    countryOfOrigin = "Country",
                    inStock = true
                )
            )
        )
    }
}
