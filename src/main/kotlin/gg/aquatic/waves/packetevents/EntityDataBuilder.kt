package gg.aquatic.waves.packetevents

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose
import com.github.retrooper.packetevents.protocol.entity.type.EntityType
import gg.aquatic.waves.packetevents.EntityDataBuilder.Companion.ANY
import gg.aquatic.waves.packetevents.EntityDataBuilder.Companion.values
import gg.aquatic.waves.packetevents.type.*
import net.kyori.adventure.text.Component
import java.util.*

open class EntityDataBuilder {

    protected val entityData = HashMap<Int, EntityData>()

    protected fun <T> addData(index: Int, type: EntityDataType<T>, value: T) {
        entityData += index to EntityData(index, type, value)
    }

    fun isOnFire(boolean: Boolean) {
        setFlag(0x01, boolean)
    }

    fun isSneaking(boolean: Boolean) {
        setFlag(0x02, boolean)
    }

    fun isSprinting(boolean: Boolean) {
        setFlag(0x08, boolean)
    }

    fun isSwimming(boolean: Boolean) {
        setFlag(0x010, boolean)
    }

    fun isInvisible(boolean: Boolean) {
        setFlag(0x20, boolean)
    }

    fun isGlowing(boolean: Boolean) {
        setFlag(0x40, boolean)
    }

    fun isFlying(boolean: Boolean) {
        setFlag((0x80).toByte(), boolean)
    }

    fun setCustomName(name: Component?) {
        addData(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.ofNullable(name))
    }

    fun isCustomNameVisible(boolean: Boolean) {
        addData(3, EntityDataTypes.BOOLEAN, boolean)
    }

    fun isSilent(boolean: Boolean) {
        addData(4, EntityDataTypes.BOOLEAN, boolean)
    }

    fun hasNoGravity(boolean: Boolean) {
        addData(5, EntityDataTypes.BOOLEAN, boolean)
    }

    fun setPose(pose: EntityPose) {
        addData(5, EntityDataTypes.ENTITY_POSE, pose)
    }

    private fun setFlag(flag: Byte, isSet: Boolean) {
        val previous = entityData[0]
        var previousByte = previous?.value as? Byte ?: 0x00.toByte()
        previousByte = if (isSet) {
            (previousByte.toInt() or flag.toInt()).toByte()
        } else {
            (previousByte.toInt() and flag.toInt().inv()).toByte()
        }
        addData(0, EntityDataTypes.BYTE, previousByte)
    }

    fun build(): List<EntityData> {
        return entityData.values.toList()
    }

    companion object {
        val values = HashMap<String, () -> EntityDataBuilder>()

        // BASES

        val DISPLAY: DisplayEntityDataBuilder
            get() {
                return DisplayEntityDataBuilder()
            }
        val ANY: EntityDataBuilder
            get() {
                return EntityDataBuilder()
            }

        // SPECIFICS

        val BLOCK_DISPLAY = register("BLOCK_DISPLAY") {
            BlockDisplayEntityDataBuilder()
        }
        val ITEM_DISPLAY = register("ITEM_DISPLAY") {
            ItemDisplayEntityDataBuilder()
        }
        val TEXT_DISPLAY = register("TEXT_DISPLAY") {
            TextDisplayEntityDataBuilder()
        }
        val INTERACTION = register("INTERACTION") {
            InteractionEntityDataBuilder()
        }
        val ITEM = register("ITEM") {
            ItemEntityDataBuilder()
        }

        private fun <T : EntityDataBuilder> register(name: String, builder: () -> T): () -> T {
            values += name to builder
            return builder
        }
    }
}

fun EntityType.toEntityDataBuilder(): EntityDataBuilder {
    val builder = values[this.name.key.uppercase()] ?: return ANY
    return builder()
}

fun org.bukkit.entity.EntityType.toEntityDataBuilder(): EntityDataBuilder {
    val builder = values[this.name.uppercase()] ?: return ANY
    return builder()
}