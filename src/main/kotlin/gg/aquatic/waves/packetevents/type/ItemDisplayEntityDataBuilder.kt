package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.inventory.ItemStack

class ItemDisplayEntityDataBuilder: DisplayEntityDataBuilder() {

    fun setItem(item: ItemStack) {
        addData(
            23,EntityDataTypes.ITEMSTACK,SpigotConversionUtil.fromBukkitItemStack(item)
        )
    }
    fun setItemTransformation(transform: ItemDisplayTransform) {
        addData(24,EntityDataTypes.BYTE,transform.ordinal.toByte())
    }

}