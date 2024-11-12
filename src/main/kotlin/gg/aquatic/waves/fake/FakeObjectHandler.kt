package gg.aquatic.waves.fake

import gg.aquatic.aquaticseries.lib.chunkcache.ChunkCacheHandler
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationCacheHandler
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationChunkObject
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.runAsync
import gg.aquatic.aquaticseries.lib.util.runAsyncTimer
import gg.aquatic.waves.Waves
import gg.aquatic.waves.chunk.PlayerChunkLoadEvent
import gg.aquatic.waves.chunk.PlayerChunkUnloadEvent
import gg.aquatic.waves.chunk.chunkId
import gg.aquatic.waves.chunk.trackedByPlayers
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
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
        event<ChunkUnloadEvent> {
            val obj = ChunkCacheHandler.getObject(it.chunk, LocationChunkObject::class.java) as? LocationChunkObject
                ?: return@event
            for ((_, locMap) in obj.cache) {
                for ((_, inst) in locMap) {
                    if (inst !is FakeObject) {
                        continue
                    }
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
                    tickableObjects += inst
                }
            }
        }
        event<PlayerInteractEvent> {
            val block = it.clickedBlock ?: return@event
            val bundle = ChunkCacheHandler.getObject(
                block.location.chunk,
                FakeObjectChunkBundle::class.java
            ) as? FakeObjectChunkBundle ?: return@event
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
        fakeObject.isViewing -= player
        if (removeViewer) {
            fakeObject.viewers -= player
        }

        if (fakeObject.location.chunk.trackedByPlayers().none { fakeObject.viewers.contains(it) } && fakeObject.isViewing.isEmpty()) {
            objectRemovalQueue += fakeObject
        }
    }

    override fun disable(waves: Waves) {

    }
}