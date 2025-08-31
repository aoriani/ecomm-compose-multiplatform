package io.aoriani.ecomm.ui.screens.productlist.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.ui.screens.productlist.productlist
import io.aoriani.ecomm.ui.test.TestTags
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
        var wasTileClicked = false
        var wasAddToCartClicked = false

        setContentWithContext {
            ProductTile(
                ProductPreview(
                    id = ProductBasic.Id("id"),
                    name = productName,
                    price = DollarAmount(productPrice),
                    thumbnailUrl = "thumbnailUrl"
                ),
                onTileClicked = { wasTileClicked = true },
                onAddToCartClicked = { wasAddToCartClicked = true }
            )
        }

        onNodeWithText(productName).assertExists().assertIsDisplayed()
        onNodeWithText("$${productPrice}").assertExists().assertIsDisplayed()
        onNodeWithTag(TestTags.screens.productlist.addTocartButton).assertExists()
            .assertIsDisplayed().assertContentDescriptionEquals("Add to Cart")


        assertFalse(wasAddToCartClicked)
        onNodeWithTag(TestTags.screens.productlist.addTocartButton).performClick()
        assertTrue(wasAddToCartClicked)

        assertFalse(wasTileClicked)
        onRoot().performClick()
        assertTrue(wasTileClicked)

    }
}
