package gg.aquatic.waves.packetevents

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import gg.aquatic.waves.packetevents.type.BlockDisplayEntityDataBuilder
import gg.aquatic.waves.packetevents.type.ItemDisplayEntityDataBuilder
import gg.aquatic.waves.packetevents.type.TextDisplayEntityDataBuilder

abstract class EntityDataBuilder {

    protected val entityData = HashMap<Int, EntityData>()

    protected fun addData(entityData: EntityData) {
        this.entityData += entityData.index to entityData
    }

    fun build(): List<EntityData> {
        return entityData.values.toList()
    }

    companion object {
        val BLOCK_DISPLAY: BlockDisplayEntityDataBuilder
            get() {
                return BlockDisplayEntityDataBuilder()
            }
        val ITEM_DISPLAY: ItemDisplayEntityDataBuilder
            get() {
                return ItemDisplayEntityDataBuilder()
            }
        val TEXT_DISPLAY: TextDisplayEntityDataBuilder
            get() {
                return TextDisplayEntityDataBuilder()
            }
    }
}