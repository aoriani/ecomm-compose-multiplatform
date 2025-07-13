package io.aoriani.ecomm.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.menu_help
import ecommerceapp.composeapp.generated.resources.menu_help_mnemonic
import ecommerceapp.composeapp.generated.resources.menu_item_about
import ecommerceapp.composeapp.generated.resources.menu_item_about_mnemonic
import org.jetbrains.compose.resources.stringResource

@Composable
fun FrameWindowScope.DesktopMenuBar(onAboutMenuItemClicked: () -> Unit = {}) {
    MenuBar {
        Menu(
            text = stringResource(Res.string.menu_help),
            mnemonic = stringResource(Res.string.menu_help_mnemonic).first()
        ) {
            Item(
                stringResource(Res.string.menu_item_about),
                mnemonic = stringResource(Res.string.menu_item_about_mnemonic).first(),
                onClick = onAboutMenuItemClicked)
        }
    }
}
