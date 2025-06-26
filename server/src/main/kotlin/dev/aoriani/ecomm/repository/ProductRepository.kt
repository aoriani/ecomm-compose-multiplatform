package dev.aoriani.ecomm.repository

import dev.aoriani.ecomm.graphql.models.Product

interface ProductRepository {
    fun getAll(): List<Product>
    fun getById(id: String): Product?
}
