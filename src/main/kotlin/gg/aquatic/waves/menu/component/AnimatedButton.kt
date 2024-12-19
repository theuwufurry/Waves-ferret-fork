package gg.aquatic.waves.menu.component

import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.MenuComponent
import org.bukkit.inventory.ItemStack

class AnimatedButton: MenuComponent() {
    override val id: String
        get() = TODO("Not yet implemented")
    override val priority: Int
        get() = TODO("Not yet implemented")
    override val slots: Collection<Int>
        get() = TODO("Not yet implemented")
    override val onClick: (AsyncPacketInventoryInteractEvent) -> Unit
        get() = TODO("Not yet implemented")
    private val itemstack: ItemStack?
        get() = TODO("Not yet implemented")

    override fun itemstack(menu: AquaticMenu): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun tick(menu: AquaticMenu) {
        TODO("Not yet implemented")
    }
}