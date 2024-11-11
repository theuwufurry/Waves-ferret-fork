package gg.aquatic.waves.fake

import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData
import gg.aquatic.aquaticseries.lib.chunkcache.ChunkCacheHandler
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationCacheHandler
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationChunkObject
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.runAsyncTimer
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.util.packetEvent
import gg.aquatic.waves.util.player
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.util.concurrent.ConcurrentHashMap

object FakeObjectHandler : WaveModule {
    override val type: WaveModules = WaveModules.FAKE_OBJECTS

    internal val tickableObjects = ConcurrentHashMap.newKeySet<FakeObject>()
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

        packetEvent<PacketSendEvent>(priority = PacketListenerPriority.LOWEST) {
            if (packetType != PacketType.Play.Server.CHUNK_DATA) {
                return@packetEvent
            }

            val packet = WrapperPlayServerChunkData(this)
            val player = player()
            val chunk = player.world.getChunkAt(packet.column.x, packet.column.z)
            val obj = ChunkCacheHandler.getObject(chunk, LocationChunkObject::class.java) as? LocationChunkObject
                ?: return@packetEvent
            for ((_, locMap) in obj.cache) {
                for ((_, inst) in locMap) {
                    if (inst !is FakeObject) {
                        continue
                    }
                    if (inst.viewers.contains(player)) {
                        inst.loadedChunkViewers += player
                    }
                    tickableObjects += inst
                }
            }
        }
        event<PlayerQuitEvent> {
            handlePlayerRemove(it.player)
        }
        event<ChunkUnloadEvent> {
            val obj = ChunkCacheHandler.getObject(it.chunk, LocationChunkObject::class.java) as? LocationChunkObject
                ?: return@event
            for ((_, locMap) in obj.cache) {
                for ((_, inst) in locMap) {
                    if (inst !is FakeObject) {
                        continue
                    }
                    inst.isViewing.clear()
                    inst.loadedChunkViewers.clear()
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
                    tickableObjects += inst
                }
            }
        }
        event<PlayerInteractEvent> {
            val block = it.clickedBlock ?: return@event
            val bundle = LocationCacheHandler.getObject(block.location, FakeObjectLocationBundle::class.java) as? FakeObjectLocationBundle ?: return@event
            it.isCancelled = true
            for (block1 in bundle.blocks) {
                if (block1.viewers.contains(it.player)) {
                    block1.show(it.player)
                    block1.onInteract(it)
                    break
                }
            }
        }
    }

    private fun handlePlayerRemove(player: Player) {
        for (tickableObject in tickableObjects) {
            handlePlayerRemove(player, tickableObject, true)
        }
    }

    internal fun handlePlayerRemove(player: Player, fakeObject: FakeObject, removeViewer: Boolean = false) {
        fakeObject.loadedChunkViewers -= player
        fakeObject.isViewing -= player
        if (removeViewer) {
            fakeObject.viewers -= player
        }

        if (fakeObject.loadedChunkViewers.isEmpty() && fakeObject.isViewing.isEmpty()) {
            objectRemovalQueue += fakeObject
        }
    }

    override fun disable(waves: Waves) {

    }
}