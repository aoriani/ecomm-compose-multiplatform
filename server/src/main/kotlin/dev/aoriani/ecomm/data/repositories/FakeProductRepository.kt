package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductId
import dev.aoriani.ecomm.domain.models.exceptions.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository

/**
 * A fake implementation of the [ProductRepository] interface designed for testing purposes.
 *
 * The repository provides a mechanism to define and control the behavior of the `getAll` and `getById` methods
 * through the provided lambdas. This allows simulating various scenarios such as successful data retrieval or
 * failures without relying on an actual data source.
 *
 * @constructor Creates a FakeProductRepository instance.
 * @param _getAll A lambda that defines the behavior of the [getAll] method. Defaults to returning an empty list wrapped in a successful [Result].
 * @param _getById A lambda that defines the behavior of the [getById] method. Defaults to throwing a [ProductNotFoundException] for the given product ID.
 */
internal class FakeProductRepository(
    private val _getAll: () -> Result<List<Product>> = { Result.success(emptyList()) },
    private val _getById: (ProductId) -> Result<Product> = { id -> Result.failure(ProductNotFoundException(id)) }

) : ProductRepository {
    /**
     * Retrieves all products.
     *
     * @return A [Result] containing a list of [Product]s. If successful, the result will hold the list of products;
     * otherwise, it will contain an exception describing the failure.
     */
    override suspend fun getAll(): Result<List<Product>> = _getAll()

    /**
     * Retrieves a product by its unique identifier.
     *
     * This function uses the provided lambda to simulate the retrieval of the product for testing purposes.
     * The behavior of the function is configurable through the lambda passed during repository initialization.
     *
     * @param id The unique identifier of the product to retrieve.
     * @return A [Result] object containing the retrieved [Product] on success or an exception on failure.
     */
    override suspend fun getById(id: ProductId): Result<Product> = _getById(id)
}