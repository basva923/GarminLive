package com.github.basva923.garminphoneactivity.model

class FixedSizeSet<T>(private val maxSize: Int) : MutableSet<T> {
    private var nextIndex = 0
    private val values = mutableListOf<T>()

    override fun add(element: T): Boolean {
        if (size < maxSize) {
            nextIndex = 0
            values.add(element)
        } else {
            values[nextIndex] = element
            nextIndex = (nextIndex + 1) % maxSize
        }

        return true
    }

    override fun contains(element: T): Boolean {
        return values.contains(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            add(element)
        }
        return true
    }


    override fun clear() {
        nextIndex = 0
        values.clear()
    }

    override fun iterator(): MutableIterator<T> {
        return toList().iterator()
    }

    override fun remove(element: T): Boolean {
        val i = values.indexOf(element)
        if (i == -1)
            return false

        if (nextIndex > i)
            nextIndex--
        values.removeAt(i)
        return true
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var changed = false
        for (el in elements) {
            changed = remove(el) || changed
        }
        return true
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        var changed = false
        for (el in this) {
            if (el !in elements) {
                remove(el)
                changed = true
            }
        }
        return changed
    }

    override val size: Int
        get() = values.size

    override fun containsAll(elements: Collection<T>): Boolean {
        return values.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return values.isEmpty()
    }

    fun toList(): MutableList<T> {
        val result = mutableListOf<T>()
        result.addAll(values.subList(nextIndex, values.size))
        result.addAll(values.subList(0, nextIndex))
        return result
    }


}