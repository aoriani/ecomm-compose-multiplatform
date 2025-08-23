package io.aoriani.ecomm.domain

import io.aoriani.ecomm.data.model.ProductBasic
import io.aoriani.ecomm.data.repositories.cart.CartRepository

/**
 * Represents the use case for adding a product to the shopping cart.
 *
 * This interface defines a contract for adding a [ProductBasic] item to the cart.
 * It extends the [UseCase] interface, specifying [ProductBasic] as the input type and [Unit] as the output type,
 * indicating that the operation does not return a specific value upon completion.
 */
fun interface AddToCartUseCase: UseCase<ProductBasic, Unit> {
    /**
     * Adds the given product to the shopping cart.
     *
     * @param input The product to add.
     */
    override suspend operator fun invoke(input: ProductBasic)
}


/**
 * Concrete implementation of [AddToCartUseCase] that adds a product to the shopping cart.
 *
 * This class uses the provided [CartRepository] to perform the addition operation.
 *
 * @property cartRepository The repository responsible for managing the shopping cart data.
 */
class AddToCartUseCaseImpl(
    private val cartRepository: CartRepository
): AddToCartUseCase {
    /**
     * Adds the given product to the shopping cart.
     *
     * @param product The product to add.
     */
    override suspend operator fun invoke(input: ProductBasic) {
        cartRepository.add(input)
    }
}