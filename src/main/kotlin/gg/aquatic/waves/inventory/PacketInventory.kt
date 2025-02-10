package gg.aquatic.waves.inventory

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import gg.aquatic.waves.util.toUser
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

open class PacketInventory(
    title: Component,
    val type: InventoryType
) : Cloneable {

    val viewers: ConcurrentHashMap<UUID, InventoryViewer> = ConcurrentHashMap<UUID, InventoryViewer>()
    val content: ConcurrentHashMap<Int, ItemStack> = ConcurrentHashMap<Int, ItemStack>()

    var title = title
        set(value) {
            field = value
            inventoryOpenPacket = updateTitle()
        }

    var inventoryOpenPacket: WrapperPlayServerOpenWindow = updateTitle()
        private set

    private fun updateTitle(): WrapperPlayServerOpenWindow {
        val packet = WrapperPlayServerOpenWindow(126, type.id(), title)

        for ((_, viewer) in viewers) {
            viewer.player.toUser()?.sendPacket(packet)
            InventoryManager.updateInventoryContent(this, viewer)
        }

        return packet
    }


    internal fun addItem(slot: Int, item: ItemStack) {
        val previous = content[slot]
        if (previous != null) {
            if (previous.isSimilar(item) && previous.amount == item.amount) {
                return
            }
        }
        content[slot] = item
    }

    fun setItem(slot: Int, item: ItemStack?) {
        InventoryManager.updateItem(this, item, slot)
    }
    fun changeItems(items: Map<Int,ItemStack?>) {
        InventoryManager.updateItems(this, items)
    }
    fun setItems(items: Map<Int, ItemStack>) {
        this.content.clear()
        this.content.putAll(items)
        for ((_, viewer) in viewers) {
            InventoryManager.updateInventoryContent(this, viewer)
        }
    }

    fun updateItems(player: Player) {
        val viewer = viewers[player.uniqueId] ?: return
        InventoryManager.updateInventoryContent(this, viewer)
    }

    fun updateItems() {
        for ((_, viewer) in viewers) {
            InventoryManager.updateInventoryContent(this, viewer)
        }
    }

    override fun clone(): PacketInventory {
        val inv = PacketInventory(title, type)
        val clonedMap = ConcurrentHashMap<Int, ItemStack>()
        content.forEach { (key, value) -> clonedMap[key] = value.clone() }
        inv.content.putAll(clonedMap)
        return inv
    }
}