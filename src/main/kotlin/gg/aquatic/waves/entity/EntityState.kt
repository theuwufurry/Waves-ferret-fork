package gg.aquatic.waves.entity

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.UUID

class EntityState(
    val entityUUID: UUID
) {

    var isRemoved: Boolean = false
    var cancelOtherData = false
    private var _entityData: MutableMap<Int,EntityData> = java.util.HashMap()
    val equipment = HashMap<EquipmentSlot,Equipment>()
    var team: ScoreBoardTeamInfo? = null

    var entityData: Map<Int,EntityData>
    get() = _entityData
    set(value) {
        _entityData = value.toMutableMap()
    }

    fun addEntityData(data: EntityData) {
        val previous = _entityData[data.index]
        if (previous != null) {
            previous.value = data.value
            return
        }
        _entityData[data.index] = data
    }
    fun clearEntityData() {
        _entityData.clear()
    }

    fun setGlow(glow: Boolean, color: NamedTextColor? = null) {
        addEntityData(EntityData(0, EntityDataTypes.BYTE, (if (glow) 0x40 else 0x00).toByte()))
        if (glow) {
            if (team == null) {
                team = ScoreBoardTeamInfo(
                    Component.text(UUID.randomUUID().toString()),
                    null,
                    null,
                    WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                    WrapperPlayServerTeams.CollisionRule.NEVER,
                    color,
                    WrapperPlayServerTeams.OptionData.NONE
                )
            }
            else {
                team!!.color = color
            }
        }
    }
    fun setGlow(color: NamedTextColor) {
        setGlow(true, color)
    }

}