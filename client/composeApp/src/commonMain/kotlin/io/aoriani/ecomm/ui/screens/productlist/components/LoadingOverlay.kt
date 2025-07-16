package io.aoriani.ecomm.ui.screens.productlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import io.aoriani.ecomm.ui.screens.productlist.productlist
import io.aoriani.ecomm.ui.test.TestTags
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Box(modifier = modifier) {
        content()
        if (isLoading) {
            Box(
                modifier = Modifier.background(Color(0x80000000)).fillMaxSize()
                    .testTag(TestTags.screens.productlist.loadingOverlay),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}


@Preview
@Composable
fun LoadingOverlayLoadingPreview() {
    MaterialTheme {
        LoadingOverlay(isLoading = true) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hello")
            }
        }
    }
}