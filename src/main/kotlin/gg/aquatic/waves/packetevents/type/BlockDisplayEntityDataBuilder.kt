package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Material
import org.bukkit.block.data.BlockData

class BlockDisplayEntityDataBuilder : DisplayEntityDataBuilder() {

    fun setBlockState(material: Material) {
        setBlockState(material.createBlockData())
    }
    fun setBlockState(blockData: BlockData) {
        addData(
            EntityData(
                23,
                EntityDataTypes.BLOCK_STATE,
                SpigotConversionUtil.fromBukkitBlockData(blockData).globalId
            )
        )
    }

}