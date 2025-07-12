package io.aoriani.ecomm.data.repositories.products

import kotlin.time.Duration.Companion.minutes

class MemProductDataSource {
    private val timeToLive = 1.minutes
    private val maxEntries = 5
}