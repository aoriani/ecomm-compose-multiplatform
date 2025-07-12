package io.aoriani.ecomm.data.repositories.products

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A generic class that wraps a value of type [T] and associates it with a creation time and a time-to-live (TTL).
 * This allows checking if the cached value has expired.
 *
 * @param T The type of the value being cached.
 * @property value The actual data being cached.
 * @property creationTime The [Instant] when this cached entry was created.
 * @property timeToLive The [Duration] for which this cached entry is considered valid.
 * @property now A function that returns the current [Instant]. Defaults to [Clock.System.now]. This is primarily for testing purposes.
 * @property expired A boolean indicating whether the cached value has passed its time-to-live.
 */
@OptIn(ExperimentalTime::class)
class Cached<out T>(
    val value: T,
    private val creationTime: Instant,
    private val timeToLive: Duration,
    private val now: () -> Instant = Clock.System::now
) {
    val expired: Boolean get() = (now() - creationTime) > timeToLive
}
