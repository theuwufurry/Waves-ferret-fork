package gg.aquatic.waves.entity

import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.mapPair
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.util.packetEvent
import gg.aquatic.waves.util.player
import gg.aquatic.waves.util.toUser
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object EntityHandler : WaveModule {

    val states = HashMap<UUID, HashMap<Int, EntityState>>()
    val globalStates = HashMap<Int, EntityState>()

    fun updateEntity(player: Player, entityId: Int) {
        val playerStates = states[player.uniqueId] ?: return
        val state = playerStates[entityId]
        if (state != null) {
            updateEntity(player, entityId, state)
        }
        val globalState = globalStates[entityId]
        if (globalState != null) {
            updateEntity(player, entityId, globalState)
        }
    }

    private fun updateEntity(player: Player, entityId: Int, state: EntityState) {
        val user = player.toUser()
        if (state.isRemoved) {
            val packet = WrapperPlayServerDestroyEntities(entityId)
            user.sendPacket(packet)
            return
        }
        val packet = WrapperPlayServerEntityMetadata(entityId, state.entityData.values.toMutableList())
        user.sendPacket(packet)
        if (state.equipment.isNotEmpty()) {
            val equipmentPacket = WrapperPlayServerEntityEquipment(entityId, state.equipment.values.toMutableList())
            user.sendPacket(equipmentPacket)
        }
        if (state.team != null) {
            val teamPacket = WrapperPlayServerTeams(
                UUID.randomUUID().toString(),
                WrapperPlayServerTeams.TeamMode.CREATE,
                state.team,
                state.entityUUID.toString()
            )
            user.sendPacket(teamPacket)
        }
    }

    override val type: WaveModules = WaveModules.ENTITIES

    fun modifyEntity(player: Player, entity: Entity, modifier: EntityState.() -> Unit) {
        val state = states.getOrPut(player.uniqueId) { HashMap() }
            .getOrPut(entity.entityId) { EntityState(entity.uniqueId) }
        modifier(state)
        updateEntity(player, entity.entityId)
    }

    fun modifyEntity(entity: Entity, modifier: EntityState.() -> Unit) {
        val state = globalStates.getOrPut(entity.entityId) { EntityState(entity.uniqueId) }
        modifier(state)
    }

    fun clearStates(player: Player) {
        val playerStates = states[player.uniqueId] ?: return
        playerStates.clear()
    }

    override suspend fun initialize(waves: Waves) {
        event<PlayerQuitEvent> {
            states.remove(it.player.uniqueId)
        }
        event<EntityDeathEvent> {
            for ((_, playerStates) in states) {
                playerStates.remove(it.entity.entityId)
            }
        }

        packetEvent<PacketSendEvent>(PacketListenerPriority.LOWEST) {
            when (packetType) {
                PacketType.Play.Server.ENTITY_METADATA -> {
                    val packet = WrapperPlayServerEntityMetadata(this)

                    val playerStates = states[user.uuid]
                    val state = playerStates?.get(packet.entityId) ?: globalStates[packet.entityId]
                    if (state != null) {
                        if (state.isRemoved) {
                            isCancelled = true
                            return@packetEvent
                        }

                        val previousData = packet.entityMetadata.mapPair { it.index to it }
                        if (state.cancelOtherData) {
                            previousData.clear()
                        }
                        previousData.putAll(state.entityData)
                        packet.entityMetadata = previousData.values.toMutableList()
                    }
                }

                PacketType.Play.Server.ENTITY_EQUIPMENT -> {
                    val packet = WrapperPlayServerEntityEquipment(this)

                    val playerStates = states[user.uuid]
                    val state = playerStates?.get(packet.entityId) ?: globalStates[packet.entityId] ?: return@packetEvent

                    if (state.isRemoved) {
                        isCancelled = true
                        return@packetEvent
                    }

                    val previousEquipment = packet.equipment.mapPair { it.slot to it }
                    if (state.cancelOtherData) {
                        previousEquipment.clear()
                    }
                    state.equipment.putAll(previousEquipment)

                }

                PacketType.Play.Server.SPAWN_ENTITY -> {
                    val packet = WrapperPlayServerSpawnEntity(this)

                    val playerStates = states[user.uuid]
                    val state = playerStates?.get(packet.entityId) ?: globalStates[packet.entityId] ?: return@packetEvent
                    if (state.isRemoved) {
                        isCancelled = true
                        return@packetEvent
                    }
                    updateEntity(player(), packet.entityId)
                }
            }
        }
    }

    override fun disable(waves: Waves) {
        states.clear()
    }

}