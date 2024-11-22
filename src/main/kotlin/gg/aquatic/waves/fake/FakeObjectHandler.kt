package gg.aquatic.waves.fake

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import gg.aquatic.aquaticseries.lib.chunkcache.ChunkCacheHandler
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationChunkObject
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.runAsync
import gg.aquatic.aquaticseries.lib.util.runAsyncTimer
import gg.aquatic.waves.Waves
import gg.aquatic.waves.chunk.PlayerChunkLoadEvent
import gg.aquatic.waves.chunk.PlayerChunkUnloadEvent
import gg.aquatic.waves.chunk.chunkId
import gg.aquatic.waves.chunk.trackedByPlayers
import gg.aquatic.waves.fake.block.FakeBlock
import gg.aquatic.waves.fake.block.FakeBlockInteractEvent
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.FakeEntityInteractEvent
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.util.packetEvent
import gg.aquatic.waves.util.player
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.util.concurrent.ConcurrentHashMap

object FakeObjectHandler : WaveModule {
    override val type: WaveModules = WaveModules.FAKE_OBJECTS

    internal val tickableObjects = ConcurrentHashMap.newKeySet<FakeObject>()
    internal val idToEntity = ConcurrentHashMap<Int, FakeEntity>()
    internal val locationToBlock = ConcurrentHashMap<Location, FakeBlock>()
    val objectRemovalQueue = ConcurrentHashMap.newKeySet<FakeObject>()

    override fun initialize(waves: Waves) {
        runAsyncTimer(
            100, 1
        ) {
            if (tickableObjects.isNotEmpty()) {
                tickableObjects -= objectRemovalQueue
                objectRemovalQueue.clear()
            }
            for (tickableObject in tickableObjects) {
                tickableObject.handleTick()
            }
        }

        event<PlayerChunkLoadEvent> {
            runAsync {
                val obj =
                    ChunkCacheHandler.getObject(it.chunk, FakeObjectChunkBundle::class.java) as? FakeObjectChunkBundle
                        ?: return@runAsync
                tickableObjects += obj.blocks
                tickableObjects += obj.entities
            }
        }
        event<PlayerChunkUnloadEvent> {
            runAsync {
                for (tickableObject in tickableObjects) {
                    if (tickableObject.location.chunk.chunkId() != it.chunk.chunkId()) continue
                    handlePlayerRemove(it.player, tickableObject, false)
                }
            }
        }

        event<PlayerQuitEvent> {
            handlePlayerRemove(it.player)
        }
        event<PlayerJoinEvent> {
            for (tickableObject in tickableObjects) {
                if (tickableObject.audience.canBeApplied(it.player)) {
                    tickableObject.addViewer(it.player)
                }
            }
        }
        /*
        event<ChunkUnloadEvent> {
            val obj = ChunkCacheHandler.getObject(it.chunk, FakeObjectChunkBundle::class.java) as? FakeObjectChunkBundle
                ?: return@event

            for ((_, locMap) in obj.cache) {
                for ((_, inst) in locMap) {
                    if (inst !is FakeObject) {
                        continue
                    }
                    inst.viewers.clear()
                    inst.isViewing.clear()
                    objectRemovalQueue += inst
                }
            }
        }
        event<ChunkLoadEvent> {
            val obj = ChunkCacheHandler.getObject(it.chunk, LocationChunkObject::class.java) as? LocationChunkObject
                ?: return@event
            for ((_, locMap) in obj.cache) {
                for ((_, inst) in locMap) {
                    if (inst !is FakeObject) {
                        continue
                    }
                    inst.audience = inst.audience
                    tickableObjects += inst
                }
            }
        }
         */
        event<PlayerInteractEvent> {
            val block = locationToBlock[it.clickedBlock?.location ?: return@event] ?: return@event
            it.isCancelled = true
            if (block.viewers.contains(it.player)) {
                block.show(it.player)
                val event = FakeBlockInteractEvent(
                    block,
                    it.player,
                    it.action == Action.LEFT_CLICK_BLOCK || it.action == Action.LEFT_CLICK_AIR
                )
                block.onInteract(event)
            }
        }
        packetEvent<PacketReceiveEvent> {
            if (this.packetType != Play.Client.INTERACT_ENTITY) return@packetEvent
            val packet = WrapperPlayClientInteractEntity(this)
            val entity = idToEntity[packet.entityId] ?: return@packetEvent
            val event = FakeEntityInteractEvent(
                entity,
                this.player(),
                (packet.action == WrapperPlayClientInteractEntity.InteractAction.ATTACK)
            )
            entity.onInteract(event)
        }
    }

    private fun handlePlayerRemove(player: Player) {
        for (tickableObject in tickableObjects) {
            handlePlayerRemove(player, tickableObject, true)
        }
    }

    internal fun handlePlayerRemove(player: Player, fakeObject: FakeObject, removeViewer: Boolean = false) {
        fakeObject.isViewing -= player
        if (removeViewer) {
            fakeObject.viewers -= player
        }

        if (fakeObject.location.chunk.trackedByPlayers().isEmpty() && fakeObject.isViewing.isEmpty()) {
            objectRemovalQueue += fakeObject
        }
    }

    override fun disable(waves: Waves) {

    }
}