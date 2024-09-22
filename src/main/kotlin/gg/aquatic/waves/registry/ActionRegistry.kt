package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.action.AbstractAction

inline fun <reified T: Any> WavesRegistry.registerAction(id: String, action: AbstractAction<T>) {
    val map = ACTION.getOrPut(T::class.java) { HashMap() }
    map += id to action
}

inline fun <reified T: Any> WavesRegistry.getAction(id: String): AbstractAction<T>? {
    val map = ACTION[T::class.java] ?: return null
    return map[id] as AbstractAction<T>
}

inline fun <reified T: Any> AbstractAction<T>.register(id: String) {
    WavesRegistry.registerAction(id, this)
}