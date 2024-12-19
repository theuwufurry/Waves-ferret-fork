package gg.aquatic.waves.menu

import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import org.bukkit.inventory.ItemStack

abstract class MenuComponent(
) {

    abstract val id: String
    abstract val priority: Int
    abstract val slots: Collection<Int>
    abstract val onClick: (AsyncPacketInventoryInteractEvent) -> Unit
    //abstract val itemstack: ItemStack?

    abstract fun itemstack(menu: AquaticMenu): ItemStack?

    abstract fun tick(menu: AquaticMenu)
}