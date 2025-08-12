/**
 * Domain entry point for listing all products.
 *
 * Encapsulates the application logic to fetch every available product, delegating to the
 * repository layer and returning results using Kotlin's [Result] type.
 */
package dev.aoriani.ecomm.domain.usecases

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.repositories.ProductRepository

/**
 * Use case that retrieves all available products.
 *
 * Delegates to the underlying repository and wraps the outcome in [Result].
 */
class GetAllProductsUseCase(private val repository: ProductRepository) : ResultUseCase<Unit, List<Product>> {
    /**
     * Executes the use case.
     *
     * @param params Unused; present to conform to the [UseCase] contract.
     * @return A [Result] containing the list of products on success, or a failure if retrieval fails.
     */
    override suspend fun invoke(params: Unit): Result<List<Product>> = repository.getAll()
}