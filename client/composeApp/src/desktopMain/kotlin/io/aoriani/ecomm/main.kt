package io.aoriani.ecomm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.aoriani.ecomm.ui.window.AboutAppDialog
import io.aoriani.ecomm.ui.window.DesktopMenuBar

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "eCommerceApp",
    ) {
        var isAboutDialogVisible by remember { mutableStateOf(false) }
        DesktopMenuBar(onAboutMenuItemClicked = { isAboutDialogVisible = true })
        App()
        AboutAppDialog(
            isVisible = isAboutDialogVisible,
            onCloseRequest = { isAboutDialogVisible = false })
    }
}
