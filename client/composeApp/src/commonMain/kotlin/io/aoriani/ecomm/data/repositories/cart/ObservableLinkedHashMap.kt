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
    interface OnChangeListener<V> {
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

    override fun clear() {
        delegateMap.clear()
        onChangeListener?.onChange(values.toList())
    }

    override fun remove(key: K): V? {
        val result = delegateMap.remove(key)
        onChangeListener?.onChange(values.toList())
        return result
    }

    override fun putAll(from: Map<out K, V>) {
        delegateMap.putAll(from)
        onChangeListener?.onChange(values.toList())
    }

    override fun put(key: K, value: V): V? =
        delegateMap.put(key, value).also { onChangeListener?.onChange(values.toList()) }
}