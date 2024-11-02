package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority

fun PacketListener.register(priority: PacketListenerPriority = PacketListenerPriority.NORMAL) {
    PacketEvents.getAPI().eventManager.registerListener(this, priority)
}