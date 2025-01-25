package gg.aquatic.waves.packetevents

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType
import gg.aquatic.waves.packetevents.type.*

abstract class EntityDataBuilder {

    protected val entityData = HashMap<Int, EntityData>()

    protected fun <T> addData(index: Int, type: EntityDataType<T>, value: T) {
        entityData += index to EntityData(index, type, value)
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
        val ANY: BaseEntityDataBuilder
            get() {
                return BaseEntityDataBuilder()
            }
        val INTERACTION: InteractionEntityDataBuilder
            get() {
                return InteractionEntityDataBuilder()
            }
        val ITEM: ItemEntityDataBuilder
            get() {
                return ItemEntityDataBuilder()
            }
    }
}