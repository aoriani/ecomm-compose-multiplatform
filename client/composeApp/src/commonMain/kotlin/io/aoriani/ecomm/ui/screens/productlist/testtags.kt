package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.ui.test.TestTags

/**
 * Test tags for the Product List screen.
 * These tags are used to identify specific UI elements in tests,
 * making the tests more robust and less prone to breaking due to UI changes.
 */
object ProductListScreenTestTags {
    /**
     * Test tag for the loading overlay.
     */
    const val loadingOverlay = "productlist:loadingOverlay"

    /**
     * Test tag for the "Add to Cart" button.
     */
    const val addToCartButton = "productlist:addToCartButton"

    /**
     * Test tag for the cart count badge.
     */
    const val cartCountBadge = "productlist:cartCountBadge"
}

/**
 * Extension property on [TestTags.screens] to provide easy access to the [ProductListScreenTestTags].
 */
val TestTags.screens.productlist get() = ProductListScreenTestTags
