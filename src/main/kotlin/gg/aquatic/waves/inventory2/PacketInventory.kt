package gg.aquatic.waves.inventory2

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.inventory.InventoryViewer
import gg.aquatic.waves.util.toUser
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

class PacketInventory(
    title: Component,
    val type: InventoryType
) : Cloneable {

    val viewers: ConcurrentHashMap<UUID, InventoryViewer> = ConcurrentHashMap<UUID, InventoryViewer>()

    var title = title
        set(value) {
            field = value
            viewers.forEach {
                //it.openInventory(this)
            }
        }

    var inventoryOpenPacket: WrapperPlayServerOpenWindow = updateTitle()

    private fun updateTitle(): WrapperPlayServerOpenWindow {
        val packet = WrapperPlayServerOpenWindow(126, type.id(), title)

        for ((_, viewer) in viewers) {
            viewer.player.toUser().sendPacket(packet)
        }

        return packet
    }

    val content: ConcurrentHashMap<Int, ItemStack> = ConcurrentHashMap<Int, ItemStack>()

    fun addItem(slot: Int, item: ItemStack) {
        val previous = content[slot]
        if (previous != null) {
            if (previous.isSimilar(item) && previous.amount == item.amount) {
                return
            }
        }
        content[slot] = item
    }

    override fun clone(): PacketInventory {
        val inv = PacketInventory(title, type)
        val clonedMap = ConcurrentHashMap<Int, ItemStack>()
        content.forEach { (key, value) -> clonedMap[key] = value.clone() }
        inv.content.putAll(clonedMap)
        return inv
    }
}