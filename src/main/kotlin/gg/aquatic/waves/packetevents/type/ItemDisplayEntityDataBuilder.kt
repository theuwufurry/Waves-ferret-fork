package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.inventory.ItemStack

class ItemDisplayEntityDataBuilder: DisplayEntityDataBuilder() {

    fun setItem(item: ItemStack): ItemDisplayEntityDataBuilder {
        addData(
            23,EntityDataTypes.ITEMSTACK,SpigotConversionUtil.fromBukkitItemStack(item)
        )
        return this
    }
    fun setItemTransformation(transform: ItemDisplayTransform): ItemDisplayEntityDataBuilder {
        addData(24,EntityDataTypes.BYTE,transform.ordinal.toByte())
        return this
    }

}