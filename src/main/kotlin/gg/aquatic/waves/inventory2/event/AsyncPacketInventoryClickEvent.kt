package gg.aquatic.waves.inventory2.event

import gg.aquatic.aquaticseries.lib.util.AquaticEvent
import gg.aquatic.waves.inventory.ButtonType
import gg.aquatic.waves.inventory.ClickType
import gg.aquatic.waves.inventory.InventoryViewer
import gg.aquatic.waves.inventory2.InventoryItem
import gg.aquatic.waves.inventory2.PacketInventory
import org.bukkit.event.Cancellable
import org.bukkit.inventory.ItemStack
import java.util.Optional

class AsyncPacketInventoryClickEvent(
    val viewer: InventoryViewer,
    var cursor: ItemStack?,
    val inventory: PacketInventory,
    val slot: Int,
    val buttonType: ButtonType,
    val clickType: ClickType,
    //var item: InventoryItem,
    val slots: Optional<MutableMap<Int,InventoryItem>>
) : AquaticEvent(true), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }
}