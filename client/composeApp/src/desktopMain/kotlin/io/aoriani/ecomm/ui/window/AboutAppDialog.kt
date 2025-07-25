package io.aoriani.ecomm.ui.window

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.dialog_about_title
import io.aoriani.ecomm.ui.test.TestTagsDesktop
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutAppDialog(isVisible: Boolean, onCloseRequest: () -> Unit = {}) {
    if (isVisible) {
        DialogWindow(
            onCloseRequest = onCloseRequest,
            state = rememberDialogState(
                position = WindowPosition(
                    Alignment.Companion.Center
                )
            ),
            title = stringResource(Res.string.dialog_about_title),
            resizable = false,
        ) {
            Text(
                text = "TODO - Implement Content",
                modifier = Modifier.testTag(TestTagsDesktop.aboutDialog)
            )
        }
    }
}
