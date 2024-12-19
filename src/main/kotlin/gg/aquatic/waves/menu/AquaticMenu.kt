package gg.aquatic.waves.menu

import gg.aquatic.waves.inventory.InventoryManager
import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.inventory.PacketInventory
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

open class AquaticMenu(
    title: Component,
    type: InventoryType,
): PacketInventory(title,type) {

    val components = ConcurrentHashMap<String,MenuComponent>()
    val renderedComponents = ConcurrentHashMap<Int,String>()

    open fun open(player: Player) {
        InventoryManager.openMenu(player, this)
    }

    internal fun updateComponent(component: MenuComponent) {
        val item = component.itemstack(this)
        for (slot in component.slots) {
            val previousComponentId = renderedComponents[slot]
            val previousComponent = components[previousComponentId]
            val currentItem = this.content[slot]
            if (previousComponent == null) {
                renderedComponents[slot] = component.id
                if (currentItem != null) {
                    if (currentItem.isSimilar(item)) {
                        continue
                    }
                }
                this.setItem(slot,item)
                continue
            }
            if (previousComponent.priority < component.priority) {
                renderedComponents[slot] = component.id
                if (currentItem != null) {
                    if (currentItem.isSimilar(item)) {
                        continue
                    }
                }
                this.setItem(slot,item)
            }
        }
    }

    internal fun tick() {
        for (value in components.values) {
            value.tick(this)
        }
    }

    internal fun onInteract(event: AsyncPacketInventoryInteractEvent) {
        renderedComponents[event.slot]?.let {
            components[it]?.onClick?.invoke(event)
        }
    }
}