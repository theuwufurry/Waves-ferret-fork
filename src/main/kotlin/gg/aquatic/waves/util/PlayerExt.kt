package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenBook
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import gg.aquatic.waves.entity.EntityHandler
import io.github.retrooper.packetevents.adventure.serializer.json.JSONComponentSerializer
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import io.th0rgal.oraxen.utils.ReflectionUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.BookMeta

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

fun Player.sendItemChange(slot: Int, itemStack: ItemStack) {
    val packet = WrapperPlayServerSetSlot(0,0,slot,SpigotConversionUtil.fromBukkitItemStack(itemStack))
    toUser().sendPacket(packet)
}

fun Player.openBook(pages: List<Component>) {
    val bookItem = ItemStack(Material.WRITTEN_BOOK)
    val bookMeta = bookItem.itemMeta as BookMeta
    val newPages = pages.map {
        ComponentSerializer.parse(JSONComponentSerializer.json().serialize(it))
    }
    bookMeta.spigot().pages = newPages
    bookItem.itemMeta = bookMeta

    val previousItem = this.inventory.itemInMainHand
    sendItemChange(this.inventory.heldItemSlot, bookItem)
    val packet = WrapperPlayServerOpenBook(InteractionHand.MAIN_HAND)
    toUser().sendPacket(packet)
    sendItemChange(this.inventory.heldItemSlot, previousItem)
}