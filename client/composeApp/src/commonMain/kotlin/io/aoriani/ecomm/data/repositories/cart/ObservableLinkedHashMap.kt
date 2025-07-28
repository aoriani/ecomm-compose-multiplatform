package io.aoriani.ecomm.data.repositories.cart

/**
 * A [LinkedHashMap] that allows observing changes.
 *
 * This class wraps a [LinkedHashMap] and provides an [OnChangeListener] interface
 * that can be used to listen for modifications to the map.
 *
 * @param K The type of the keys in the map.
 * @param V The type of the values in the map.
 * @property delegateMap The underlying [LinkedHashMap] instance. Defaults to an empty [linkedMapOf].
 */
internal class ObservableLinkedHashMap<K, V>(private val delegateMap: LinkedHashMap<K, V> = linkedMapOf()) :
    MutableMap<K, V> by delegateMap {

    /**
     * Interface definition for a callback to be invoked when the map changes.
     */
    fun interface OnChangeListener<V> {
        fun onChange(values: List<V>)
    }

    /**
     * A listener that is notified whenever the map is changed.
     * The listener is notified after the change has been made.
     *
     * Example:
     * ```
     * val map = ObservableLinkedHashMap<String, Int>()
     * map.onChangeListener = object : ObservableLinkedHashMap.OnChangeListener {
     *    override fun onChange() {
     *       println("Map changed: $map")
     *   }
     * }
     * map["a"] = 1 // Prints "Map changed: {a=1}"
     * ```
     */
    var onChangeListener: OnChangeListener<V>? = null

    /**
     * Clears all key-value pairs from the map.
     * After clearing, the [onChangeListener] is invoked with an empty list.
     */
    override fun clear() {
        delegateMap.clear()
        onChangeListener?.onChange(values.toList())
    }

    /**
     * Removes the specified [key] and its corresponding value from the map.
     * If the key was present, the [onChangeListener] is invoked after removal.
     *
     * @param key The key to be removed.
     * @return The previous value associated with the key, or `null` if the key was not present.
     */
    override fun remove(key: K): V? {
        val result = delegateMap.remove(key)
        onChangeListener?.onChange(values.toList())
        return result
    }

    /**
     * Copies all of the mappings from the specified [from] map to this map.
     * These mappings will replace any mappings that this map had for any of the keys currently in the specified map.
     * After all mappings are copied, the [onChangeListener] is invoked.
     *
     * @param from The map whose mappings are to be placed in this map.
     */
    override fun putAll(from: Map<out K, V>) {
        delegateMap.putAll(from)
        onChangeListener?.onChange(values.toList())
    }

    /**
     * Associates the specified [value] with the specified [key] in the map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     * After the mapping is updated, the [onChangeListener] is invoked.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @return The previous value associated with the key, or `null` if the key was not present.
     */
    override fun put(key: K, value: V): V? =
        delegateMap.put(key, value).also { onChangeListener?.onChange(values.toList()) }
}