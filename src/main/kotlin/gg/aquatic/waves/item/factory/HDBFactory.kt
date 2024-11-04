package gg.aquatic.waves.item.factory

import gg.aquatic.waves.item.ItemHandler
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.inventory.ItemStack

object HDBFactory: ItemHandler.Factory {
    override fun create(id: String): ItemStack? {
        return HeadDatabaseAPI().getItemHead(id)
    }
}