package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun Component.broadcast() {
    for (user in PacketEvents.getAPI().protocolManager.users) {
        user.sendMessage(this)
    }
}

fun Component.toMMString(): String {
    return MiniMessage.miniMessage().serialize(this)
}

fun Component.toJson(): String {
    return net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(this)
}

fun Component.toPlain(): String {
    return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(this)
}