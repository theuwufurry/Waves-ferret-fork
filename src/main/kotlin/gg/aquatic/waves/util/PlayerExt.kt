package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import net.kyori.adventure.text.Component
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

fun Player.toUser(): User {
    return PacketEvents.getAPI().playerManager.getUser(this)
}

fun Player.sendMessage(vararg components: Component) {
    val user = toUser()
    for (component in components) {
        user.sendMessage(component)
    }
}

fun Player.showGlow(entity: Entity, visible: Boolean) {
    val packet = WrapperPlayServerEntityMetadata(entity.entityId, mutableListOf(
        EntityData(0, EntityDataTypes.BYTE, (if (visible) 0x40 else 0x00).toByte())
    ))
    toUser().sendPacket(packet)
}