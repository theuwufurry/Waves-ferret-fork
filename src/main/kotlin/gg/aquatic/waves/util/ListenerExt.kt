package gg.aquatic.waves.util

import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

fun Listener.unregister() {
    HandlerList.unregisterAll(this)
}