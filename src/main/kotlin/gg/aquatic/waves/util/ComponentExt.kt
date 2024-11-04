package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import net.kyori.adventure.text.Component

fun Component.broadcast() {
    for (user in PacketEvents.getAPI().protocolManager.users) {
        user.sendMessage(this)
    }
}