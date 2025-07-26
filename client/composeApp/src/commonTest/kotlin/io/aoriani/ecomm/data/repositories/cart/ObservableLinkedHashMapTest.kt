package io.aoriani.ecomm.data.repositories.cart

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObservableLinkedHashMapTest {

    private class TestOnChangeListener<V> : ObservableLinkedHashMap.OnChangeListener<V> {
        var onChangeCallCount = 0
        var lastReceivedValues: List<V>? = null
        override fun onChange(values: List<V>) {
            onChangeCallCount++
            lastReceivedValues = values
        }
    }

    @Test
    fun `When initialized with default constructor then map is empty and listener is null`() {
        val map = ObservableLinkedHashMap<String, Int>()
        assertTrue(map.isEmpty())
        assertEquals(0, map.size)
        assertNull(map.onChangeListener)
    }

    @Test
    fun `When initialized with delegate map then map contains delegate elements`() {
        val initialMap = linkedMapOf("one" to 1, "two" to 2)
        val map = ObservableLinkedHashMap(initialMap)
        assertEquals(2, map.size)
        assertEquals(1, map["one"])
        assertEquals(2, map["two"])
        assertFalse(map.isEmpty())
    }

    @Test
    fun `When onChangeListener is set then it is correctly assigned`() {
        val map = ObservableLinkedHashMap<String, Int>()
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener
        assertNotNull(map.onChangeListener)
        assertEquals(listener, map.onChangeListener)
    }

    @Test
    fun `When clear is called then map is empty and listener is notified`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1, "two" to 2))
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        map.clear()

        assertTrue(map.isEmpty())
        assertEquals(0, map.size)
        assertEquals(1, listener.onChangeCallCount)
        assertTrue(listener.lastReceivedValues?.isEmpty() == true)
    }

    @Test
    fun `Given empty map When clear is called then map remains empty and listener is notified`() {
        val map = ObservableLinkedHashMap<String, Int>()
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        map.clear()

        assertTrue(map.isEmpty())
        assertEquals(1, listener.onChangeCallCount)
        assertTrue(listener.lastReceivedValues?.isEmpty() == true)
    }

    @Test
    fun `When remove is called with existing key then element is removed value is returned and listener is notified`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1, "two" to 2))
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        val removedValue = map.remove("one")

        assertEquals(1, removedValue)
        assertEquals(1, map.size)
        assertNull(map["one"])
        assertEquals(2, map["two"])
        assertEquals(1, listener.onChangeCallCount)
        assertEquals(listOf(2), listener.lastReceivedValues)
    }

    @Test
    fun `When remove is called with non-existing key then null is returned and listener is notified`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1))
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        val removedValue = map.remove("two")

        assertNull(removedValue)
        assertEquals(1, map.size)
        assertEquals(1, map["one"])
        assertEquals(1, listener.onChangeCallCount) // Notifies even if key not found
        assertEquals(listOf(1), listener.lastReceivedValues)
    }

    @Test
    fun `When putAll is called then all elements are added and listener is notified`() {
        val map = ObservableLinkedHashMap<String, Int>()
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        val sourceMap = mapOf("one" to 1, "two" to 2)
        map.putAll(sourceMap)

        assertEquals(2, map.size)
        assertEquals(1, map["one"])
        assertEquals(2, map["two"])
        assertEquals(1, listener.onChangeCallCount)
        assertEquals(listOf(1, 2), listener.lastReceivedValues?.sorted()) // Order might vary for toList() from map values
    }

    @Test
    fun `When putAll is called with existing keys then values are overwritten and listener is notified`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 0, "three" to 3)) // Initial order: one, three
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        val sourceMap = mapOf("one" to 1, "two" to 2) // Puts "one", then "two"
        map.putAll(sourceMap) // Map becomes: one=1, three=3, two=2 (LinkedHashMap preserves insertion order of new keys)


        assertEquals(3, map.size)
        assertEquals(1, map["one"])
        assertEquals(2, map["two"])
        assertEquals(3, map["three"])
        assertEquals(1, listener.onChangeCallCount)
        assertEquals(listOf(1, 3, 2), listener.lastReceivedValues) // Expected: one, three, two
    }

    @Test
    fun `When putAll is called with an empty map then listener is notified`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1))
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        map.putAll(emptyMap())

        assertEquals(1, map.size)
        assertEquals(1, listener.onChangeCallCount)
        assertEquals(listOf(1), listener.lastReceivedValues)
    }


    @Test
    fun `When put is called with new key then element is added null is returned and listener is notified`() {
        val map = ObservableLinkedHashMap<String, Int>()
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        val previousValue = map.put("one", 1)

        assertNull(previousValue)
        assertEquals(1, map.size)
        assertEquals(1, map["one"])
        assertEquals(1, listener.onChangeCallCount)
        assertEquals(listOf(1), listener.lastReceivedValues)
    }

    @Test
    fun `When put is called with existing key then value is updated old value is returned and listener is notified`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1))
        val listener = TestOnChangeListener<Int>()
        map.onChangeListener = listener

        val previousValue = map.put("one", 100)

        assertEquals(1, previousValue)
        assertEquals(1, map.size)
        assertEquals(100, map["one"])
        assertEquals(1, listener.onChangeCallCount)
        assertEquals(listOf(100), listener.lastReceivedValues)
    }

    // Test inherited MutableMap properties and methods
    @Test
    fun `Given map with elements When get is called with existing key then correct value is returned`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1))
        assertEquals(1, map["one"])
    }

    @Test
    fun `Given map When get is called with non-existing key then null is returned`() {
        val map = ObservableLinkedHashMap<String, Int>()
        assertNull(map["nonexistent"])
    }

    @Test
    fun `Given map with elements When containsKey is called with existing key then returns true`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1))
        assertTrue(map.containsKey("one"))
    }

    @Test
    fun `Given map When containsKey is called with non-existing key then returns false`() {
        val map = ObservableLinkedHashMap<String, Int>()
        assertFalse(map.containsKey("nonexistent"))
    }

    @Test
    fun `Given map with elements When containsValue is called with existing value then returns true`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1))
        assertTrue(map.containsValue(1))
    }

    @Test
    fun `Given map When containsValue is called with non-existing value then returns false`() {
        val map = ObservableLinkedHashMap<String, Int>()
        assertFalse(map.containsValue(100))
    }

    @Test
    fun `Given an empty map When isEmpty is called then returns true`() {
        val map = ObservableLinkedHashMap<String, Int>()
        assertTrue(map.isEmpty())
    }

    @Test
    fun `Given a non-empty map When isEmpty is called then returns false`() {
        val map = ObservableLinkedHashMap(linkedMapOf("one" to 1))
        assertFalse(map.isEmpty())
    }

    @Test
    fun `When elements are added or map is empty then size returns correct count`() {
        val map = ObservableLinkedHashMap<String, Int>()
        assertEquals(0, map.size)
        map.put("one", 1)
        assertEquals(1, map.size)
        map.put("two", 2)
        assertEquals(2, map.size)
    }

    @Test
    fun `Given map with elements When entries is called then correct entry set is returned`() {
        val initialData = linkedMapOf("one" to 1, "two" to 2)
        val map = ObservableLinkedHashMap(initialData)
        assertEquals(initialData.entries, map.entries)
    }

    @Test
    fun `Given map with elements When keys is called then correct key set is returned`() {
        val initialData = linkedMapOf("one" to 1, "two" to 2)
        val map = ObservableLinkedHashMap(initialData)
        assertEquals(initialData.keys, map.keys)
    }

    @Test
    fun `Given map with elements When values is called then correct value collection is returned`() {
        val initialData = linkedMapOf("one" to 1, "two" to 2)
        val map = ObservableLinkedHashMap(initialData)
        // Order matters for LinkedHashMap values if comparing directly as lists
        assertEquals(initialData.values.toList(), map.values.toList())
    }
}
