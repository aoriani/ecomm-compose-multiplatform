package io.aoriani.ecomm.domain

import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.repositories.cart.CartRepository

/**
 * Adds a product to the shopping cart.
 *
 * @property cartRepository The repository for managing the shopping cart.
 */
class AddToCartUseCase(
    private val cartRepository: CartRepository
) {
    /**
     * Adds the given product to the shopping cart.
     *
     * @param product The product to add.
     */
    suspend operator fun invoke(product: ProductBasic) {
        cartRepository.add(product)
    }
}