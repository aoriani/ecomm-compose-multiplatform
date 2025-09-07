package io.aoriani.ecomm.ui.screens.productlist

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import ecommerceapp.composeapp.generated.resources.Res
import ecommerceapp.composeapp.generated.resources.content_description_cart
import io.aoriani.ecomm.data.model.DollarAmount
import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.ui.test.TestTags
import io.aoriani.ecomm.ui.test.UiTest
import io.aoriani.ecomm.ui.test.setContentWithContext
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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

        onNodeWithTag(TestTags.screens.productlist.cartCountBadge).assertDoesNotExist()
    }

    @Test
    fun `When state is loading and cart count is not zero a badge is displayed`() =
        runComposeUiTest {

            setContentWithContext {
                ProductListScreen(ProductListUiState.Loading(cartItemCount = 14))
            }

            onNodeWithTag(TestTags.screens.productlist.cartCountBadge)
                .assertIsDisplayed()
                .onChildren().assertAny(hasText("14"))
        }

    @Test
    fun `When state is error and cart count is zero no badge is displayed`() = runComposeUiTest {

        setContentWithContext {
            ProductListScreen(ProductListUiState.Error(cartItemCount = 0))
        }

        onNodeWithTag(TestTags.screens.productlist.cartCountBadge).assertDoesNotExist()
    }

    @Test
    fun `When state is error and cart count is not zero a badge is displayed`() =
        runComposeUiTest {

            setContentWithContext {
                ProductListScreen(ProductListUiState.Error(cartItemCount = 14))
            }

            onNodeWithTag(TestTags.screens.productlist.cartCountBadge)
                .assertIsDisplayed()
                .onChildren().assertAny(hasText("14"))
        }

    @Test
    fun `When state is error snackbar is displayed`() = runComposeUiTest {
        var wasRetryClicked = false
        setContentWithContext {
            ProductListScreen(ProductListUiState.Error(cartItemCount = 0, _reload = {
                wasRetryClicked = true
            }))
        }
        awaitIdle()
        // SnackBar Test
        onNodeWithText("Something went wrong").assertIsDisplayed()
        onNodeWithText("Retry").assertIsDisplayed().performClick()
        assertTrue(wasRetryClicked)
    }

    @Test
    fun `When state is success and cart count is zero no badge is displayed`() = runComposeUiTest {

        setContentWithContext {
            ProductListScreen(ProductListUiState.Success(cartItemCount = 0))
        }

        onNodeWithTag(TestTags.screens.productlist.cartCountBadge).assertDoesNotExist()
    }

    @Test
    fun `When state is success and cart count is not zero a badge is displayed`() =
        runComposeUiTest {

            setContentWithContext {
                ProductListScreen(ProductListUiState.Success(cartItemCount = 14))
            }

            onNodeWithTag(TestTags.screens.productlist.cartCountBadge)
                .assertIsDisplayed()
                .onChildren().assertAny(hasText("14"))
        }

    @Test
    fun `When state is success tapping on tile will navigate to product details`() =
        runComposeUiTest {
            var wouldNavigateToProductDetails = false
            var tappedProduct: ProductBasic? = null

            val producName = "Product 1"
            setContentWithContext {
                ProductListScreen(
                    ProductListUiState.Success(
                        cartItemCount = 14, products = persistentListOf(
                            ProductPreview(
                                id = ProductBasic.Id("id"),
                                name = producName,
                                price = DollarAmount("10.0"),
                                thumbnailUrl = "https://picsum.photos/200/300"
                            )
                        )
                    ), navigateToProductDetails = {
                        wouldNavigateToProductDetails = true
                        tappedProduct = it
                    }
                )
            }

            onNodeWithText(producName).performClick()
            assertTrue(wouldNavigateToProductDetails)
            assertNotNull(tappedProduct)
            assertEquals(producName, tappedProduct.name)
        }

    @Test
    fun `When state is success tapping on add to cart will invoke the correspondent callback`() =
        runComposeUiTest {
            var wasAddToCartTapped = false
            var tappedProduct: ProductBasic? = null

            val producName = "Product 1"
            setContentWithContext {
                ProductListScreen(
                    ProductListUiState.Success(
                        cartItemCount = 14, products = persistentListOf(
                            ProductPreview(
                                id = ProductBasic.Id("id"),
                                name = producName,
                                price = DollarAmount("10.0"),
                                thumbnailUrl = "https://picsum.photos/200/300"
                            )
                        ),
                        _addToCart = {
                            wasAddToCartTapped = true
                            tappedProduct = it
                        }

                    )
                )
            }
            onNodeWithContentDescription("Add to Cart").assertIsDisplayed().performClick()
            assertTrue(wasAddToCartTapped)
            assertNotNull(tappedProduct)
            assertEquals(producName, tappedProduct.name)
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