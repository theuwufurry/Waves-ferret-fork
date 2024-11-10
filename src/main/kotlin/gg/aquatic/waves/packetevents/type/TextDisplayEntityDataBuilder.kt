package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import net.kyori.adventure.text.Component
import org.bukkit.entity.TextDisplay.TextAlignment
import kotlin.experimental.and
import kotlin.experimental.or

class TextDisplayEntityDataBuilder: DisplayEntityDataBuilder() {

    fun setText(text: Component) {
        addData(EntityData(23,EntityDataTypes.ADV_COMPONENT,text))
    }
    fun setLineWidth(width: Int) {
        addData(EntityData(24,EntityDataTypes.INT,width))
    }
    fun setBackgroundColor(color: Int) {
        addData(EntityData(25,EntityDataTypes.INT,color))
    }
    fun setTextOpacity(opacity: Byte) {
        addData(EntityData(26,EntityDataTypes.BYTE,opacity))
    }
    fun hasShadow(boolean: Boolean) {
        val previous = entityData[27]
        val previousByte = previous?.value as? Byte ?: 0x00.toByte()
        if ((previousByte and 0x01.toByte()) == 0x01.toByte()) {
            if (boolean) return
            addData(EntityData(27,EntityDataTypes.BYTE,(previousByte - 0x01.toByte()).toByte()))
            return
        }
        addData(EntityData(27,EntityDataTypes.BYTE,(previousByte + 0x01.toByte()).toByte()))
    }
    fun isSeeThrough(boolean: Boolean) {
        val previous = entityData[27]
        val previousByte = previous?.value as? Byte ?: 0x00.toByte()
        if ((previousByte and 0x02.toByte()) == 0x02.toByte()) {
            if (boolean) return
            addData(EntityData(27,EntityDataTypes.BYTE,(previousByte - 0x02.toByte()).toByte()))
            return
        }
        addData(EntityData(27,EntityDataTypes.BYTE,(previousByte + 0x02.toByte()).toByte()))
    }
    fun useDefaultBackgroundColor(boolean: Boolean) {
        val previous = entityData[27]
        val previousByte = previous?.value as? Byte ?: 0x00.toByte()
        if ((previousByte and 0x04.toByte()) == 0x04.toByte()) {
            if (boolean) return
            addData(EntityData(27,EntityDataTypes.BYTE,(previousByte - 0x04.toByte()).toByte()))
            return
        }
        addData(EntityData(27,EntityDataTypes.BYTE,(previousByte + 0x04.toByte()).toByte()))
    }
    fun setTextAlignment(alignment: TextAlignment) {
        val previous = entityData[27]
        var previousByte = previous?.value as? Byte ?: 0x00.toByte()
        if ((previousByte and 0x08.toByte()) != 0x08.toByte()) {
            previousByte = (previousByte + 0x08.toByte()).toByte()
        }
        addData(EntityData(28,EntityDataTypes.BYTE,previousByte or (alignment.ordinal shl 3).toByte()))
    }

}