package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.content_description_cart
import io.aoriani.ecomm.ui.test.TestTags
import io.aoriani.ecomm.ui.test.UiTest
import io.aoriani.ecomm.ui.test.setContentWithContext
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ProductListScreenTest : UiTest() {

    @Test
    fun `When state is loading then loading overlay is displayed`() = runComposeUiTest {

        setContentWithContext {
            ProductListScreen(ProductListUiState.Loading())
        }

        onNodeWithTag(TestTags.screens.productlist.loadingOverlay).assertIsDisplayed()
    }

    @Test
    fun `When state is loading and cart count is zero no badge is displayed`() = runComposeUiTest {

        setContentWithContext {
            ProductListScreen(ProductListUiState.Loading(cartItemCount = 0))
        }

        onNodeWithTag(TestTags.screens.productlist.cartCountBagde).assertDoesNotExist()
    }


    @Test
    fun `When state is loading and cart count is not zero no badge is displayed`() =
        runComposeUiTest {

            setContentWithContext {
                ProductListScreen(ProductListUiState.Loading(cartItemCount = 14))
            }

            onNodeWithTag(TestTags.screens.productlist.cartCountBagde)
                .assertIsDisplayed()
                .onChildren().assertAny(hasText("14"))
        }

    @Test
    fun `When cart action button is clicked the correspondent navigation callback is invoked`() =
        runComposeUiTest {
            var cartContentDescription = ""
            var wasCartClicked = false
            setContentWithContext {
                ProductListScreen(
                    ProductListUiState.Loading(),
                    navigateToCart = { wasCartClicked = true })
                cartContentDescription = stringResource(Res.string.content_description_cart)
            }

            assertFalse(wasCartClicked)
            onNodeWithContentDescription(cartContentDescription).performClick()
            assertTrue(wasCartClicked)
        }
}