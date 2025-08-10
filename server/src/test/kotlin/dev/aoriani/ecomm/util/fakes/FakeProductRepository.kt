package dev.aoriani.ecomm.util.fakes

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import kotlin.test.Ignore

@Ignore("helper class for testing")
class FakeProductRepository(
    val get_all: () -> Result<List<Product>> = { Result.success(emptyList()) },
    val get_by_id: (String) -> Result<Product> = { Result.failure(ProductNotFoundException("id")) }
) : ProductRepository {
    override suspend fun getAll(): Result<List<Product>> = get_all()
    override suspend fun getById(id: String): Result<Product> = get_by_id(id)
}