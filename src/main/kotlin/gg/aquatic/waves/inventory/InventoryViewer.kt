package gg.aquatic.waves.inventory

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class InventoryViewer(
    val player: Player,
    var carriedItem: com.github.retrooper.packetevents.protocol.item.ItemStack? = null,
    val accumulatedDrag: MutableList<AccumulatedDrag> = mutableListOf()
) {

    fun setCarriedItem(itemStack: ItemStack?, real: Boolean) {
        carriedItem = itemStack?.let { SpigotConversionUtil.fromBukkitItemStack(it) }
        if (real) player.openInventory.cursor
        else {
            val packet = WrapperPlayServerSetSlot(126,0,-1, carriedItem)
            player.toUser().sendPacket(packet)
        }
    }
}