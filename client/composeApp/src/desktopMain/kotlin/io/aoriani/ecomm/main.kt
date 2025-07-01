package io.aoriani.ecomm

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.aoriani.ecomm.di.commonModule
import io.aoriani.ecomm.ui.App
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(commonModule)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "eCommerceApp",
    ) {
        App()
    }
}