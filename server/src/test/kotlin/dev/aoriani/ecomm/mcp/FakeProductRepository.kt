package dev.aoriani.ecomm.mcp

import dev.aoriani.ecomm.graphql.models.Product
import dev.aoriani.ecomm.repository.ProductRepository
import kotlin.test.Ignore

@Ignore("helper class for testing")
class FakeProductRepository(
    val get_all: () -> List<Product> = { emptyList() },
    val get_by_id: (String) -> Product? = { null }
) : ProductRepository {
    override suspend fun getAll(): List<Product> = get_all()
    override suspend fun getById(id: String): Product? = get_by_id(id)
}