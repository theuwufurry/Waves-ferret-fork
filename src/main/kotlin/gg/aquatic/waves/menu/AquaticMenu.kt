package gg.aquatic.waves.menu

import gg.aquatic.waves.inventory.InventoryType
import net.kyori.adventure.text.Component
import java.util.concurrent.ConcurrentHashMap

open class AquaticMenu(
    title: Component,
    val type: InventoryType,
) {

    val components = ConcurrentHashMap<String,MenuComponent>()

    internal fun updateComponent(component: MenuComponent) {

    }

}