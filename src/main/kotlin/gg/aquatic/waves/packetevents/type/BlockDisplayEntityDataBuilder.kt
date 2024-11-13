package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Material
import org.bukkit.block.data.BlockData

class BlockDisplayEntityDataBuilder : DisplayEntityDataBuilder() {

    fun setBlockState(material: Material): BlockDisplayEntityDataBuilder {
        setBlockState(material.createBlockData())
        return this
    }

    fun setBlockState(blockData: BlockData): BlockDisplayEntityDataBuilder {
        addData(
            23,
            EntityDataTypes.BLOCK_STATE,
            SpigotConversionUtil.fromBukkitBlockData(blockData).globalId
        )
        return this
    }

}