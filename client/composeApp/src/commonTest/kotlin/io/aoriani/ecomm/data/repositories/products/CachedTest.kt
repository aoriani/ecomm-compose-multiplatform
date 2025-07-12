package io.aoriani.ecomm.data.repositories.products

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class CachedTest {

    private val testValue = "test-value"
    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val ttl = 60.seconds

    @Test
    fun `When Cached is initialized then value is accessible`() {
        val now = startTime
        // Act
        val cached = Cached(
            value = testValue,
            creationTime = now,
            timeToLive = ttl
        )

        // Assert
        assertEquals(testValue, cached.value)
    }

    @Test
    fun `When current time is before expiration then expired is false`() {
        // Mock the clock to return a time that's before expiration (30 seconds later)
        val mockClock =
            { Instant.fromEpochMilliseconds(31000) } // 30 seconds (30,000 ms) after 1,000 ms

        // Act
        val cached = Cached(
            value = testValue,
            creationTime = startTime,
            timeToLive = ttl,
            now = mockClock
        )

        // Assert
        assertFalse(cached.expired)
    }

    @Test
    fun `When current time is after expiration then expired is true`() {
        // Mock the clock to return a time that's after expiration (70 seconds later)
        val mockClock =
            { Instant.fromEpochMilliseconds(71000) } // 70 seconds (70,000 ms) after 1,000 ms

        // Act
        val cached = Cached(
            value = testValue,
            creationTime = startTime,
            timeToLive = ttl,
            now = mockClock
        )

        // Assert
        assertTrue(cached.expired)
    }

    @Test
    fun `When current time is exactly at expiration then expired is false`() {
        // Mock the clock to return a time that's exactly at expiration (60 seconds later)
        val mockClock =
            { Instant.fromEpochMilliseconds(61000) } // 60 seconds (60,000 ms) after 1,000 ms

        // Act
        val cached = Cached(
            value = testValue,
            creationTime = startTime,
            timeToLive = ttl,
            now = mockClock
        )

        // Assert
        assertFalse(cached.expired)
    }

    @Test
    fun `When current time is just after expiration then expired is true`() {
        // Mock the clock to return a time that's just after expiration (60 seconds + 1 millisecond later)
        val mockClock =
            { Instant.fromEpochMilliseconds(61001) } // 60 seconds (60,000 ms) + 1 ms after 1,000 ms

        // Act
        val cached = Cached(
            value = testValue,
            creationTime = startTime,
            timeToLive = ttl,
            now = mockClock
        )

        // Assert
        assertTrue(cached.expired)
    }
}
