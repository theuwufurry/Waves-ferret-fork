package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.requirement.AbstractRequirement

inline fun <reified T: Any> WavesRegistry.registerRequirement(id: String, requirement: AbstractRequirement<T>) {
    val map = REQUIREMENT.getOrPut(T::class.java) { HashMap() }
    map += id to requirement
}

inline fun <reified T: Any> WavesRegistry.getRequirement(id: String): AbstractRequirement<T>? {
    val map = REQUIREMENT[T::class.java] ?: return null
    return map[id] as AbstractRequirement<T>
}

inline fun <reified T: Any> AbstractRequirement<T>.register(id: String) {
    WavesRegistry.registerRequirement(id, this)
}