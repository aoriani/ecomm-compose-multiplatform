package dev.aoriani.ecomm.graphql.exceptions

class ProductNotFoundException(id: String) : RuntimeException("Product with ID '$id' not found.")
