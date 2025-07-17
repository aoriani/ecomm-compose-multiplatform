package dev.aoriani.ecomm.graphql.exceptions

import com.expediagroup.graphql.generator.scalars.ID

class ProductNotFoundException(id: ID) : RuntimeException("Product with ID '$id' not found.")
