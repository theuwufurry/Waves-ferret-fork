package gg.aquatic.waves.util

import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.requirement.ConfiguredRequirement

inline fun <T,A,B> Collection<T>.mapPair(processor: (T) -> Pair<A,B>): MutableMap<A,B> {
    val map = mutableMapOf<A,B>()
    this.forEach {
        map += processor(it)
    }
    return map
}

inline fun <reified T: Any> Collection<ConfiguredRequirement<T>>.checkRequirements(binder: T): Boolean {
    for (configuredRequirement in this) {
        if (!configuredRequirement.execute(binder, { _, str -> str})) return false
    }
    return true
}
inline fun <reified T: Any, B> Collection<ConfiguredExecutableObject<T, B>>.executeActions(binder: T, noinline textUpdater: (T, String) -> String) {
    for (configuredAction in this) {
        configuredAction.execute(binder, textUpdater)
    }
}

inline fun <T,A,B> Collection<T>.mapPairNotNull(processor: (T) -> Pair<A,B>?): MutableMap<A,B> {
    val map = mutableMapOf<A,B>()
    for (it in this) {
        map += processor(it) ?: continue
    }
    return map
}