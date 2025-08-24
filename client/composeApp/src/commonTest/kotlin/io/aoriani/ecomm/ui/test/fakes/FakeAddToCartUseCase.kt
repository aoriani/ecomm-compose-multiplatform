package io.aoriani.ecomm.ui.test.fakes

import io.aoriani.ecomm.data.model.ProductBasic

class FakeAddToCartUseCase{
    var wasInvoked = false
    var product: ProductBasic? = null
    suspend operator fun invoke(product: ProductBasic) {
        wasInvoked = true
        this.product = product
    }
}