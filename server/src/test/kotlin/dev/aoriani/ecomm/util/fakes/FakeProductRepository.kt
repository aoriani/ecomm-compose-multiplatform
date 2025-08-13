package dev.aoriani.ecomm.util.fakes

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import kotlin.test.Ignore

@Ignore("helper class for testing")
class FakeProductRepository(
    val get_all: () -> Result<List<Product>> = { Result.success(emptyList()) },
    val get_by_id: (ProductId) -> Result<Product> = { Result.failure(ProductNotFoundException(ProductId("id"))) }
) : ProductRepository {
    override suspend fun getAll(): Result<List<Product>> = get_all()
    override suspend fun getById(id: ProductId): Result<Product> = get_by_id(id)
}