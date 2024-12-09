package gg.aquatic.waves.menu.component

import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.menu.MenuComponent
import org.bukkit.inventory.ItemStack

class AnimatedButton: MenuComponent() {
    override val id: String
        get() = TODO("Not yet implemented")
    override val pririty: Int
        get() = TODO("Not yet implemented")
    override val slots: Collection<Int>
        get() = TODO("Not yet implemented")
    override val onClick: (AsyncPacketInventoryInteractEvent) -> Unit
        get() = TODO("Not yet implemented")
    override val itemstack: ItemStack?
        get() = TODO("Not yet implemented")

    override fun tick() {
        TODO("Not yet implemented")
    }
}