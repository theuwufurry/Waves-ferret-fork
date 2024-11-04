package gg.aquatic.waves.item

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun Material.toCustomItem(): AquaticItem {
    return ItemHandler.create(ItemStack(this))
}