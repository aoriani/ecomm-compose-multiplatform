package io.aoriani.ecomm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ecommerceapp.composeapp.generated.resources.Res
import io.aoriani.ecomm.ui.window.AboutAppDialog
import io.aoriani.ecomm.ui.window.DesktopMenuBar
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "eCommerceApp",
        icon = painterResource(Res.drawable.appIcon)
    ) {
        var isAboutDialogVisible by remember { mutableStateOf(false) }
        DesktopMenuBar(onAboutMenuItemClicked = { isAboutDialogVisible = true })
        App()
        AboutAppDialog(
            isVisible = isAboutDialogVisible,
            onCloseRequest = { isAboutDialogVisible = false })
    }
}
