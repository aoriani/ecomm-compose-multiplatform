package io.aoriani.ecomm.ui.screens.cart

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.cart_title
import ecommerceapp.composeapp.generated.resources.content_description_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(name = "Cart Screen", showBackground = true)
@Composable
fun CartScreenPreview() {
    MaterialExpressiveTheme {
        CartScreen()
    }
}