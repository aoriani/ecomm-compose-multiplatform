package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ProductListScreenTest {

    @Test
    fun `When testing something then it should work`() = runComposeUiTest {
        val state = ProductListUiState.Loading

        setContent {
            ProductListScreen(state)
        }
    }
}