package io.aoriani.ecomm

import androidx.compose.ui.window.ComposeUIViewController
import io.aoriani.ecomm.di.initKoin
import io.aoriani.ecomm.ui.App

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}