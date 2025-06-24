package io.aoriani.ecomm.ui.screens.cart

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
import ecommerceapp.composeapp.generated.resources.cart_title
import ecommerceapp.composeapp.generated.resources.content_description_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CartScreen(navigateBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.cart_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.content_description_back)
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
fun CartScreenPreview() {
    MaterialTheme {
        CartScreen()
    }
}