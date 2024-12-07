package gg.aquatic.waves.inventory

import org.bukkit.entity.Player

class InventoryViewer(
    val player: Player,
    var carriedItem: com.github.retrooper.packetevents.protocol.item.ItemStack? = null,
    val accumulatedDrag: MutableList<AccumulatedDrag> = mutableListOf()
) {
}