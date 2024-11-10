package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import org.bukkit.block.BlockState

class BlockDisplayEntityDataBuilder: DisplayEntityDataBuilder() {

    fun setBlockState(blockState: BlockState) {
        addData(EntityData(23, EntityDataTypes.BLOCK_STATE, blockState))
    }

}