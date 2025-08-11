package dev.aoriani.ecomm.domain.models

/**
 * Exception thrown when a product with a given ID is not found.
 * @param id The ID of the product that was not found.
 */
class ProductNotFoundException(id: String) : Exception("Product with ID '$id' not found.")