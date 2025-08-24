package io.aoriani.ecomm.ui.test.fakes

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import io.aoriani.ecomm.data.repositories.products.ProductRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class FakeProductRepository(
    private val fetchProductsLambda: suspend () -> Result<ImmutableList<ProductPreview>> = {
        Result.success(
            persistentListOf()
        )
    },
    private val getProductLambda: suspend (String) -> Result<Product?> = { Result.success(null) }
) : ProductRepository {
    override suspend fun fetchProducts(): Result<ImmutableList<ProductPreview>> {
        return fetchProductsLambda()
    }

    override suspend fun getProduct(id: String): Result<Product?> {
        return getProductLambda(id)
    }
}