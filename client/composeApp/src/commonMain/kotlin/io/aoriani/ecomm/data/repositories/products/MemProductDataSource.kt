package io.aoriani.ecomm.data.repositories.products

import io.aoriani.ecomm.data.model.Product
import io.aoriani.ecomm.data.model.ProductPreview
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

typealias CachedProductPreviews = Cached<List<ProductPreview>>
typealias CachedProduct = Cached<Product>

@OptIn(ExperimentalTime::class)
class MemProductDataSource(private val nowProvider: () -> Instant) {
    private val timeToLive = 1.minutes
    private val maxEntries = 5

    private var productPreviewListCache: CachedProductPreviews? = null
    private val productCache: LinkedHashMap<String, CachedProduct> = linkedMapOf()

    fun getProductPreviews(): CachedProductPreviews? {
        return productPreviewListCache
    }

    fun getProduct(id: String): CachedProduct? {
        return productCache[id]
    }

    fun cache(productPreviews: List<ProductPreview>) {
        productPreviewListCache =
            Cached(
                productPreviews,
                creationTime = nowProvider(),
                timeToLive = timeToLive,
                now = nowProvider
            )
    }

    fun cache(product: Product) {
        productCache[product.id] =
            Cached(
                product,
                creationTime = nowProvider(),
                timeToLive = timeToLive,
                now = nowProvider
            )
    }

    fun trim() {
        if (productPreviewListCache?.isExpired() == true) productPreviewListCache = null

        // Collect keys to remove first to avoid ConcurrentModificationException
        val keysToRemove = productCache.entries
            .filter { (_, value) -> value.isExpired() }
            .map { it.key }

        // Remove the expired entries
        keysToRemove.forEach { key ->
            productCache.remove(key)
        }
    }
}
