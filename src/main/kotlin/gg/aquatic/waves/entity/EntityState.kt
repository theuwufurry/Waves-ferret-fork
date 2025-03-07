package gg.aquatic.waves.entity

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*

class EntityState(
    val entityUUID: UUID
) {

    var isRemoved: Boolean = false
    var cancelOtherData = false
    private var _entityData: MutableMap<Int, EntityData> = java.util.HashMap()
    val equipment = HashMap<EquipmentSlot, Equipment>()
    var _team: ScoreBoardTeamInfo? = null

    var entityData: Map<Int, EntityData>
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

    fun setTeam(team: ScoreBoardTeamInfo) {
        this._team = team
    }

    fun setTeam(
        displayName: Component,
        prefix: Component? = null,
        suffix: Component? = null,
        nametagVisibility: WrapperPlayServerTeams.NameTagVisibility = WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
        collisionRule: WrapperPlayServerTeams.CollisionRule = WrapperPlayServerTeams.CollisionRule.NEVER,
        color: NamedTextColor? = null
    ) {
        setTeam(
            ScoreBoardTeamInfo(
                displayName,
                prefix,
                suffix,
                nametagVisibility,
                collisionRule,
                color,
                WrapperPlayServerTeams.OptionData.NONE
            )
        )
    }

}