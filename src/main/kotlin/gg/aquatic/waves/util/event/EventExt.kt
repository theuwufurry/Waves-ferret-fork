package gg.aquatic.waves.util.event

import gg.aquatic.waves.Waves
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.function.Consumer

fun Listener.register() {
    Waves.INSTANCE.server.pluginManager.registerEvents(this, Waves.INSTANCE)
}

inline fun <reified T : Event> event(
    ignoredCancelled: Boolean = false,
    priority: EventPriority = EventPriority.NORMAL,
    callback: Consumer<T>
): Listener {
    val listener = object : Listener {}
    Bukkit.getPluginManager().registerEvent(
        T::class.java,
        listener,
        priority,
        { _, event ->
            if (event is T) {
                callback.accept(event)
            }
        },
        Waves.INSTANCE,
        ignoredCancelled
    )
    return listener
}

fun Event.call() {
    Bukkit.getServer().pluginManager.callEvent(this)
}