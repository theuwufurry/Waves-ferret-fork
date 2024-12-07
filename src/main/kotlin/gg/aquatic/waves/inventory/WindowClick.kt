package gg.aquatic.waves.inventory

import org.bukkit.entity.Player

class WindowClick(
    val player: Player,
    val clickType: ClickType,
    val slot: Int
) {
}