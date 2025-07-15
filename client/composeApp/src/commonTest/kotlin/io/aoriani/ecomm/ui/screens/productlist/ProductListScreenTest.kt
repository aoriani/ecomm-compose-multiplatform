package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import io.aoriani.ecomm.ui.test.TestTags
import io.aoriani.ecomm.ui.test.UiTest
import io.aoriani.ecomm.ui.test.setContentWithContext
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ProductListScreenTest: UiTest() {

    @Test
    fun `When state is loading then loading overlay is displayed`() = runComposeUiTest {
        val productListUiState = ProductListUiState.Loading

        setContentWithContext {
            ProductListScreen(productListUiState)
        }

        onNodeWithTag(TestTags.loadingOverlay).assertIsDisplayed()
    }

}