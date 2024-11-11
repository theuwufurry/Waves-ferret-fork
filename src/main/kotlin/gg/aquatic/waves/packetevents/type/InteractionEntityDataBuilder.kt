package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import gg.aquatic.waves.packetevents.EntityDataBuilder

class InteractionEntityDataBuilder : EntityDataBuilder() {

    fun setWidth(width: Float) {
        addData(8, EntityDataTypes.FLOAT, width)
    }
    fun setHeight(height: Float) {
        addData(9, EntityDataTypes.FLOAT, height)
    }
    fun isResponsive(boolean: Boolean) {
        addData(10, EntityDataTypes.BOOLEAN, boolean)
    }

}