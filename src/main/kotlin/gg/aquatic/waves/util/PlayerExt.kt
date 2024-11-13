package gg.aquatic.waves.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenBook
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

fun Player.openBook(pages: List<Component>) {
    val bookItem = ItemStack(Material.WRITTEN_BOOK)
    val bookMeta = bookItem.itemMeta as BookMeta
    val newPages = pages.map {
        ComponentSerializer.parse(JSONComponentSerializer.json().serialize(it))
    }
    bookMeta.spigot().pages = newPages
    bookItem.itemMeta = bookMeta

    val previousItem = this.inventory.itemInMainHand
    this.inventory.setItemInMainHand(bookItem)
    //sendItemChange(this.inventory.heldItemSlot, bookItem)
    val packet = WrapperPlayServerOpenBook(InteractionHand.MAIN_HAND)
    toUser().sendPacket(packet)
    this.inventory.setItemInMainHand(previousItem)
    //sendItemChange(this.inventory.heldItemSlot, previousItem)
}