package io.aoriani.ecomm

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.dialog_about_title
import ecommerceapp.composeapp.generated.resources.menu_help
import ecommerceapp.composeapp.generated.resources.menu_help_mnemonic
import ecommerceapp.composeapp.generated.resources.menu_item_about
import ecommerceapp.composeapp.generated.resources.menu_item_about_mnemonic
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "eCommerceApp",
    ) {
        var isAboutDialogVisible by remember { mutableStateOf(false) }
        MenuBar {
            Menu(
                text = stringResource(Res.string.menu_help),
                mnemonic = stringResource(Res.string.menu_help_mnemonic).first()
            ) {
                Item(
                    stringResource(Res.string.menu_item_about),
                    mnemonic = stringResource(Res.string.menu_item_about_mnemonic).first(),
                    onClick = { isAboutDialogVisible = true })
            }
        }
        App()
        if (isAboutDialogVisible) {
            DialogWindow(
                onCloseRequest = { isAboutDialogVisible = false }, state = rememberDialogState(
                    position = WindowPosition(
                        Alignment.Center
                    )
                ),
                title = stringResource(Res.string.dialog_about_title),
                resizable = false,
            ) {
                Text("OLA")
            }
        }
    }
}