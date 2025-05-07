package io.aoriani.ecomm.ui.screens.productdetails

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.content_description_back
import io.aoriani.ecomm.data.model.Product
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsScreen(product: String, navigateBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = product) },
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

    }
}


@Preview
@Composable
fun ProductListScreenPreview() {
    MaterialTheme {
        ProductDetailsScreen("ty"
//            Product(
//                id = "1",
//                name = "Product 1",
//                price = 10.0,
//                description = "Description 1",
//                images = persistentListOf("https://picsum.photos/200/300")
//            )
        )
    }
}