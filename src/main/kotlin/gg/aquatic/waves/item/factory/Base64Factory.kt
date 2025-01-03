package gg.aquatic.waves.item.factory

import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.util.item.ItemEncoder
import org.bukkit.inventory.ItemStack

object Base64Factory: ItemHandler.Factory {
    override fun create(id: String): ItemStack? {
        return try {
            ItemEncoder.decode(id)
        } catch (_: Exception) {
            null
        }
    }
}