package io.aoriani.ecomm.ui.screens.productlist

import io.aoriani.ecomm.ui.test.TestTags

object ProductListScreenTestTags {
    const val loadingOverlay = "productlist:loadingOverlay"
    const val addToCartButton = "productlist:addToCartButton"
    const val cartCountBadge = "productlist:cartCountBadge"
}

val TestTags.screens.productlist get() = ProductListScreenTestTags