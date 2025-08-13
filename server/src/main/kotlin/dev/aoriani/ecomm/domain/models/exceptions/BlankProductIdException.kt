package dev.aoriani.ecomm.domain.models.exceptions

/**
 * Exception thrown when a product ID is blank.
 *
 * This exception is used to indicate that a product ID cannot be empty or consist solely of whitespace.
 * It is typically thrown during validation of a product ID, such as during the initialization of a [ProductId].
 */
class BlankProductIdException: Exception("Product ID cannot be blank")