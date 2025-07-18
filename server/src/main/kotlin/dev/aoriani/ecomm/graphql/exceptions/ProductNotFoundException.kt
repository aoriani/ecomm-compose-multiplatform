package dev.aoriani.ecomm.graphql.exceptions

import com.expediagroup.graphql.generator.scalars.ID

/**
 * Exception thrown when a product with a given ID is not found.
 * @param id The ID of the product that was not found.
 */
class ProductNotFoundException(id: ID) : RuntimeException("Product with ID '$id' not found.")
