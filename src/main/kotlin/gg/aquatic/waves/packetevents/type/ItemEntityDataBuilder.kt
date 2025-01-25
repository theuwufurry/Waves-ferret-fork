package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import gg.aquatic.waves.packetevents.EntityDataBuilder
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.inventory.ItemStack

class ItemEntityDataBuilder: EntityDataBuilder() {

    fun setItem(itemStack: ItemStack): ItemEntityDataBuilder {
        addData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack))
        return this
    }

}