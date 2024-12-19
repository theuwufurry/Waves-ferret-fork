package gg.aquatic.waves.menu

import gg.aquatic.waves.inventory.InventoryManager
import gg.aquatic.waves.inventory.InventoryType
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PrivateAquaticMenu(title: Component, type: InventoryType, val player: Player) : AquaticMenu(title, type) {

    fun open() {
        InventoryManager.openMenu(player, this)
    }

}