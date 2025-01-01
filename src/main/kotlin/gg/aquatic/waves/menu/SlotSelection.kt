package gg.aquatic.waves.menu

import kotlin.math.ceil

class SlotSelection(val slots: MutableList<Int>) {

    companion object {
        fun of(vararg slots: Int): SlotSelection {
            return SlotSelection(slots.toMutableList())
        }
        fun of(collection: Collection<Int>): SlotSelection {
            return SlotSelection(collection.toMutableList())
        }

        fun rangeOf(from: Int, to: Int): SlotSelection {
            return SlotSelection((from..to).toMutableList())
        }

        fun rect(topLeft: Int, bottomRight: Int): SlotSelection {
            if (topLeft == bottomRight) return of(topLeft)
            if (topLeft > bottomRight) return rect(bottomRight, topLeft)
            if (topLeft % 9 > bottomRight % 9) {
                val d = topLeft % 9 - bottomRight % 9
                return rect(topLeft - d, bottomRight + d)
            }

            val set: MutableList<Int> = ArrayList()
            val d = bottomRight % 9 - topLeft % 9
            var rows = ceil((bottomRight / 9f - topLeft / 9f).toDouble()).toInt()
            if (d == 0) rows++
            for (i in topLeft..topLeft + d) for (j in 0 until rows) set.add(i + 9 * j)

            return SlotSelection(set)
        }
    }

    fun containsSlot(slot: Int): Boolean {
        return slots.contains(slot)
    }

    fun getSorted(): Set<Int> {
        return slots.toSortedSet()
    }

    fun and(vararg slots: Int): SlotSelection {
        this.slots.addAll(slots.toMutableSet())
        return this
    }

    fun andRange(from: Int, to: Int): SlotSelection {
        this.slots.addAll((from..to).toMutableSet())
        return this
    }

    fun andRect(topLeft: Int, bottomRight: Int): SlotSelection {
        this.slots.addAll(rect(topLeft, bottomRight).slots)
        return this
    }

}