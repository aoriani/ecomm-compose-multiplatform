package dev.aoriani.ecomm.domain.models.exceptions

import dev.aoriani.ecomm.domain.models.ProductId

/**
 * Exception thrown when a product with a given ID is not found.
 * @param id The ID of the product that was not found.
 */
class ProductNotFoundException(id: ProductId) : Exception("Product with ID '$id' not found.")