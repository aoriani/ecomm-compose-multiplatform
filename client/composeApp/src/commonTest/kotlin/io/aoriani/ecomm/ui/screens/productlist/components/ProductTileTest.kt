package io.aoriani.ecomm.ui.screens.productlist.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.ui.test.UiTest
import io.aoriani.ecomm.ui.test.setContentWithContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ProductTileTest : UiTest() {

    @Test
    fun `Verify that product tile is rendered correctly`() = runComposeUiTest {

        val productName = "ProductName"
        val productPrice = "12.96"
        var wasClicked = false

        setContentWithContext {
            ProductTile(
                ProductPreview(
                    id = "id",
                    name = productName,
                    price = DollarAmount(productPrice),
                    thumbnailUrl = "thumbnailUrl"
                ),
                onClick = { wasClicked = true }
            )
        }

        onNodeWithText(productName).assertExists().assertIsDisplayed()
        onNodeWithText("$${productPrice}").assertExists().assertIsDisplayed()

        assertFalse(wasClicked)
        onRoot().performClick()
        assertTrue(wasClicked)
    }
}
