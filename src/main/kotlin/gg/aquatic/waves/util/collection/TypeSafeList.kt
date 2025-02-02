package gg.aquatic.waves.util.collection

import java.util.AbstractMap.SimpleEntry

class TypeSafeList<T: Any>: MutableList<T> {

    //private val valuesMap = mutableMapOf<Class<out T>, T>()
    private val valuesMap = LinkedHashMap<Class<out T>, T>() // Map for type-safe access

    val classKeys: Set<Class<out T>>
        get() = valuesMap.keys

    override val size: Int
        get() = valuesMap.size

    override fun isEmpty(): Boolean = valuesMap.isEmpty()

    override fun contains(element: T): Boolean = valuesMap.containsValue(element)

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }

    override fun get(index: Int): T = valuesMap.values.elementAt(index)

    override fun indexOf(element: T): Int = valuesMap.values.indexOf(element)

    override fun lastIndexOf(element: T): Int = valuesMap.values.lastIndexOf(element)

    override fun add(element: T): Boolean {
        valuesMap[element::class.java] = element // Keyed by the element's class
        return true
    }

    override fun add(index: Int, element: T) {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("Index: $index, Size: $size")

        // Create a new map to preserve insertion order while adding at the desired index.
        val entries = valuesMap.entries.toMutableList()
        entries.add(index, SimpleEntry(element::class.java, element))

        valuesMap.clear()
        valuesMap.putAll(entries.associate { it.toPair() })
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        for (element in elements) {
            valuesMap[element::class.java] = element
            added = true
        }
        return added
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("Index: $index, Size: $size")

        if (elements.isEmpty()) return false

        // Create a new map to preserve insertion order while adding at the desired index.
        val entries = valuesMap.entries.toMutableList()
        val newEntries = elements.map { SimpleEntry(it::class.java, it) }

        entries.addAll(index, newEntries)

        valuesMap.clear()
        valuesMap.putAll(entries.associate { it.toPair() })

        return true
    }

    override fun clear() {
        valuesMap.clear()
    }

    override fun remove(element: T): Boolean {
        // Remove the element by its class
        return valuesMap.remove(element::class.java) != null
    }

    override fun removeAt(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, Size: $size")

        val entry = valuesMap.entries.elementAt(index)
        valuesMap.remove(entry.key)
        return entry.value
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val keysToRetain = elements.map { it::class.java }.toSet()
        val originalSize = valuesMap.size

        valuesMap.entries.removeIf { it.key !in keysToRetain }

        return valuesMap.size != originalSize
    }


    override fun set(index: Int, element: T): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, Size: $size")

        // Replace the element at the given index
        val oldEntry = valuesMap.entries.elementAt(index)
        valuesMap.remove(oldEntry.key)

        // Insert the replacement element
        add(index, element)
        return oldEntry.value
    }

    override fun iterator(): MutableIterator<T> {
        return valuesMap.values.iterator()
    }

    override fun listIterator(): MutableListIterator<T> {
        return valuesMap.values.toMutableList().listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        return valuesMap.values.toMutableList().listIterator(index)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var removed = false
        for (element in elements) {
            if (valuesMap.remove(element::class.java) != null) {
                removed = true
            }
        }
        return removed
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw IndexOutOfBoundsException("fromIndex: $fromIndex, toIndex: $toIndex, Size: $size")
        }

        return valuesMap.values.toMutableList().subList(fromIndex, toIndex)
    }

}
