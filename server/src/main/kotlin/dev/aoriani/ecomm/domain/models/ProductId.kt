package dev.aoriani.ecomm.domain.models

import dev.aoriani.ecomm.domain.models.exceptions.BlankProductIdException

/**
 * Represents the unique identifier for a product.
 *
 * This inline class ensures that the product ID is always treated as a strong, type-safe primitive.
 * It validates that the ID cannot be blank during initialization.
 *
 * @throws BlankProductIdException if the ID is blank
 */
@JvmInline
value class ProductId(val id: String) {
    init {
        if (id.isBlank()) throw BlankProductIdException()
    }
}