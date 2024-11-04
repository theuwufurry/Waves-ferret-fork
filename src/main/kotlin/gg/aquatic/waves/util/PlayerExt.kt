package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.User
import gg.aquatic.waves.entity.EntityHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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

fun Player.showGlow(entity: Entity, visible: Boolean, color: NamedTextColor? = null) {
    EntityHandler.modifyEntity(this,entity) {
        setGlow(visible, color)
    }
}