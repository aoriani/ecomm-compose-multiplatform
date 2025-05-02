package io.aoriani.io.aoriani.ecomm.graphql.repository

import io.aoriani.ecomm.graphql.models.Product

object ProductRepository {
    fun getAll(): List<Product> = emptyList()
    fun getById(id: String): Product? = null
}