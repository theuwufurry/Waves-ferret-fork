package gg.aquatic.waves.packetevents.type

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import net.kyori.adventure.text.Component
import org.bukkit.entity.TextDisplay.TextAlignment

class TextDisplayEntityDataBuilder: DisplayEntityDataBuilder() {

    companion object {
        const val HAS_SHADOW = 0x01.toByte()
        const val IS_SEE_THROUGH = 0x02.toByte()
        const val USE_DEFAULT_BACKGROUND_COLOR = 0x04.toByte()
        const val ALIGNMENT_MASK = 0x08.toByte()
    }

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
        setFlag(HAS_SHADOW, boolean)
    }
    fun isSeeThrough(boolean: Boolean) {
        setFlag(IS_SEE_THROUGH, boolean)
    }
    fun useDefaultBackgroundColor(boolean: Boolean) {
        setFlag(USE_DEFAULT_BACKGROUND_COLOR, boolean)
    }
    fun setTextAlignment(alignment: TextAlignment) {
        val previous = entityData[27]
        var previousByte = previous?.value as? Byte ?: 0x00.toByte()

        previousByte = (previousByte.toInt() and ALIGNMENT_MASK.toInt().inv()).toByte()
        addData(EntityData(28,EntityDataTypes.BYTE,(previousByte.toInt() or (alignment.ordinal shl 3)).toByte()))
    }

    /*
    private fun isFlagSet(flag: Byte): Boolean {
        val previous = entityData[27]
        val previousByte = previous?.value as? Byte ?: 0x00.toByte()
        return (previousByte.toInt() and flag.toInt()) == flag.toInt()
    }
     */

    private fun setFlag(flag: Byte, isSet: Boolean) {
        val previous = entityData[27]
        var previousByte = previous?.value as? Byte ?: 0x00.toByte()
        previousByte = if (isSet) {
            (previousByte.toInt() or flag.toInt()).toByte()
        } else {
            (previousByte.toInt() and flag.toInt().inv()).toByte()
        }
        addData(EntityData(27,EntityDataTypes.BYTE,previousByte))
    }

}