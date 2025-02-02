package gg.aquatic.waves.util.argument

import gg.aquatic.waves.item.AquaticItem
import org.bukkit.util.Vector

class ObjectArguments(
    private val arguments: Map<String, Any?>
) {

    fun string(id: String, updater: (String) -> String = { it }): String? {
        return updater(arguments[id]?.toString() ?: return null)
    }

    fun int(id: String, updater: (String) -> String = { it }): Int? {
        return updater(arguments[id]?.toString() ?: return null).toIntOrNull()
    }

    fun double(id: String, updater: (String) -> String = { it }): Double? {
        return updater(arguments[id]?.toString() ?: return null).toDoubleOrNull()
    }

    fun boolean(id: String, updater: (String) -> String = { it }): Boolean? {
        return updater(arguments[id]?.toString() ?: return null).toBooleanStrictOrNull()
    }

    fun float(id: String, updater: (String) -> String = { it }): Float? {
        return updater(arguments[id]?.toString() ?: return null).toFloatOrNull()
    }

    fun long(id: String, updater: (String) -> String = { it }): Long? {
        return updater(arguments[id]?.toString() ?: return null).toLongOrNull()
    }

    fun short(id: String, updater: (String) -> String = { it }): Short? {
        return updater(arguments[id]?.toString() ?: return null).toShortOrNull()
    }

    fun byte(id: String, updater: (String) -> String = { it }): Byte? {
        return updater(arguments[id]?.toString() ?: return null).toByteOrNull()
    }

    fun vector(id: String, updater: (String) -> String = { it }): Vector? {
        val updatedStrs = updater(arguments[id]?.toString() ?: return null).split(";")

        return Vector(
            updatedStrs.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
            updatedStrs.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
            updatedStrs.getOrNull(2)?.toDoubleOrNull() ?: 0.0
        )
    }

    fun any(id: String, updater: (String) -> String = { it }): Any? {
        val value = arguments[id] ?: return null
        if (value is UpdatableObjectArgument) {
            return value.getUpdatedValue(updater)
        }
        if (value is String) {
            return updater(value)
        }
        return value
    }

    inline fun <reified T> typed(id: String, noinline updater: (String) -> String = { it }): T? {
        return any(id, updater) as? T
    }

    // Collection of Strings
    fun stringCollection(id: String, updater: (String) -> String = { it }): Collection<String>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").map { it.trim() }
    }

    // Collection of Ints
    fun intCollection(id: String, updater: (String) -> String = { it }): Collection<Int>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    // Collection of Doubles
    fun doubleCollection(id: String, updater: (String) -> String = { it }): Collection<Double>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").mapNotNull { it.trim().toDoubleOrNull() }
    }

    // Collection of Booleans
    fun booleanCollection(id: String, updater: (String) -> String = { it }): Collection<Boolean>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").mapNotNull { it.trim().toBooleanStrictOrNull() }
    }

    // Collection of Floats
    fun floatCollection(id: String, updater: (String) -> String = { it }): Collection<Float>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").mapNotNull { it.trim().toFloatOrNull() }
    }

    // Collection of Longs
    fun longCollection(id: String, updater: (String) -> String = { it }): Collection<Long>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").mapNotNull { it.trim().toLongOrNull() }
    }

    // Collection of Shorts
    fun shortCollection(id: String, updater: (String) -> String = { it }): Collection<Short>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").mapNotNull { it.trim().toShortOrNull() }
    }

    // Collection of Bytes
    fun byteCollection(id: String, updater: (String) -> String = { it }): Collection<Byte>? {
        val value = arguments[id]?.toString() ?: return null
        return updater(value).split(",").mapNotNull { it.trim().toByteOrNull() }
    }

    inline fun <reified T> typedCollection(id: String, noinline updater: (String) -> String = { it }): Collection<T>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.filterIsInstance<T>().mapNotNull {
                if (it is UpdatableObjectArgument) {
                    it.getUpdatedValue(updater) as? T
                } else it
            }
        }
        return null
    }

    fun stringOrCollection(id: String, updater: (String) -> String = { it }): Collection<String>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()) }
        }
        return listOf(updater(value.toString()))
    }

    fun intOrCollection(id: String, updater: (String) -> String = { it }): Collection<Int>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()).toIntOrNull() }
        }
        return listOfNotNull(updater(value.toString()).toIntOrNull())
    }

    fun doubleOrCollection(id: String, updater: (String) -> String = { it }): Collection<Double>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()).toDoubleOrNull() }
        }
        return listOfNotNull(updater(value.toString()).toDoubleOrNull())
    }

    fun booleanOrCollection(id: String, updater: (String) -> String = { it }): Collection<Boolean>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()).toBooleanStrictOrNull() }
        }
        return listOfNotNull(updater(value.toString()).toBooleanStrictOrNull())
    }

    fun floatOrCollection(id: String, updater: (String) -> String = { it }): Collection<Float>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()).toFloatOrNull() }
        }
        return listOfNotNull(updater(value.toString()).toFloatOrNull())
    }

    fun longOrCollection(id: String, updater: (String) -> String = { it }): Collection<Long>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()).toLongOrNull() }
        }
        return listOfNotNull(updater(value.toString()).toLongOrNull())
    }

    fun shortOrCollection(id: String, updater: (String) -> String = { it }): Collection<Short>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()).toShortOrNull() }
        }
        return listOfNotNull(updater(value.toString()).toShortOrNull())
    }

    fun byteOrCollection(id: String, updater: (String) -> String = { it }): Collection<Byte>? {
        val value = any(id, updater) ?: return null
        if (value is Collection<*>) {
            return value.mapNotNull { updater(it.toString()).toByteOrNull() }
        }
        return listOfNotNull(updater(value.toString()).toByteOrNull())
    }

}