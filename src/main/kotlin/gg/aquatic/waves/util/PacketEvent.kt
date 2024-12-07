package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.*
import org.bukkit.entity.Player

fun PacketListener.register(priority: PacketListenerPriority = PacketListenerPriority.NORMAL): PacketListenerCommon {
    return PacketEvents.getAPI().eventManager.registerListener(this, priority)
}

inline fun <reified T : PacketEvent> packetEvent(
    priority: PacketListenerPriority = PacketListenerPriority.NORMAL,
    crossinline block: T.() -> Unit
): PacketListenerCommon {
    return if (T::class == PacketReceiveEvent::class) {
        object : PacketListener {
            override fun onPacketReceive(event: PacketReceiveEvent) {
                block(event as T)
            }
        }.register(priority)
    } else {
        object : PacketListener {
            override fun onPacketSend(event: PacketSendEvent) {
                block(event as T)
            }
        }.register(priority)
    }


}

fun PacketListenerCommon.unregister() {
    PacketEvents.getAPI().eventManager.unregisterListener(this)
}

fun ProtocolPacketEvent.player(): Player? {
    return this.getPlayer() as? Player
}