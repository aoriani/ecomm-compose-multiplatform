package io.aoriani.ecomm.ui.screens.productlist.components

import androidx.compose.material.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import io.aoriani.ecomm.ui.test.TestTags
import io.aoriani.ecomm.ui.test.UiTest
import io.aoriani.ecomm.ui.test.setContentWithContext
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class LoadingOverlayTest : UiTest() {

    @Test
    fun `When isLoading is true then the loading overlay should be displayed`() = runComposeUiTest {
        setContentWithContext {
            LoadingOverlay(isLoading = true) {
                Text("Hello")
            }
        }
        onNodeWithTag(TestTags.loadingOverlay).assertExists().assertIsDisplayed()
    }

    @Test
    fun `When isLoading is false then the loading overlay should not be displayed`() = runComposeUiTest {
        setContentWithContext {
            LoadingOverlay(isLoading = false) {
                Text("Hello")
            }
        }
        onNodeWithTag(TestTags.loadingOverlay).assertDoesNotExist()
    }
}