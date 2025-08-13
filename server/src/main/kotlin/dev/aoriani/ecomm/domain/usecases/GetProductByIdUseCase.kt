package dev.aoriani.ecomm.domain.usecases

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.repositories.ProductRepository

/**
 * Use case that retrieves a single product by its unique identifier.
 *
 * This use case wraps the [ProductRepository.getById] operation to encapsulate the business logic
 * of retrieving individual products.
 */
class GetProductByIdUseCase(private val repository: ProductRepository) : ResultUseCase<ProductId, Product> {

    /**
     * Executes the use case to retrieve a product by its unique identifier.
     *
     * @param params The unique identifier of the product to retrieve.
     * @return A [Result] containing the [Product] on success, or a failure if the product is not found
     * or an error occurs.
     */
    override suspend fun invoke(params: ProductId): Result<Product> = repository.getById(params)
}