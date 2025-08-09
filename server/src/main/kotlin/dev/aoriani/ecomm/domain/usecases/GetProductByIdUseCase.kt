package dev.aoriani.ecomm.domain.usecases

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.repositories.ProductRepository

/**
 * Use case that retrieves a single product by its unique identifier.
 *
 * Delegates to the underlying repository and wraps the outcome in [Result].
 */
class GetProductByIdUseCase(private val repository: ProductRepository): ResultUseCase<String, Product> {
    /**
     * Executes the use case with the given product [params] (ID).
     *
     * @param params The unique product ID to look up.
     * @return A [Result] containing the matching [Product] if found; on success with no match, the value is `null`;
     * failures are represented as a failed [Result].
     */
    override suspend fun invoke(params: String): Result<Product> = repository.getById(params)
}