package io.aoriani.ecomm

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import io.aoriani.ecomm.ui.navigation.Navigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigation()
    }
}