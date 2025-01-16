package gg.aquatic.waves.chunk

import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.util.event.call
import gg.aquatic.waves.util.event.event
import gg.aquatic.waves.util.packetEvent
import gg.aquatic.waves.util.player
import gg.aquatic.waves.util.runAsync
import gg.aquatic.waves.util.runSync
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

object ChunkTracker : WaveModule {

    // WorldName, Chunk ID, List of players
    val chunks = ConcurrentHashMap<String, HashMap<ChunkId,MutableSet<UUID>>>()
    val playerToChunks = ConcurrentHashMap<UUID, Pair<String,MutableSet<ChunkId>>>()

    override val type: WaveModules = WaveModules.CHUNK_TRACKER

    override fun initialize(waves: Waves) {
        packetEvent<PacketSendEvent> {
            if (this.packetType != Play.Server.CHUNK_DATA) return@packetEvent
            val packet = WrapperPlayServerChunkData(this)
            val player = this.player() ?: return@packetEvent
            val chunkId = ChunkId(packet.column.x, packet.column.z)
            chunks.getOrPut(player.world.name) { hashMapOf() }.getOrPut(chunkId) { ConcurrentHashMap.newKeySet() }.add(player.uniqueId)

            val chunk = chunkId.toChunk(player.world)
            var (worldName, worldChunks) = playerToChunks.getOrPut(player.uniqueId) { player.world.name to ConcurrentHashMap.newKeySet() }
            if (worldName != player.location.world!!.name) {
                for (chunkId1 in worldChunks) {
                    val chunkList = chunks[worldName]?.get(chunkId1) ?: continue
                    chunkList -= player.uniqueId
                    runAsync {
                        AsyncPlayerChunkUnloadEvent(player,chunk).call()
                    }
                    if (chunkList.isEmpty()) {
                        chunks[worldName]?.remove(chunkId1)
                    }
                }
                worldName = player.world.name
                worldChunks = ConcurrentHashMap.newKeySet()
                playerToChunks[player.uniqueId] = worldName to worldChunks
            } else {
                val toRemove = mutableSetOf<ChunkId>()
                for (chunkId1 in worldChunks) {
                    val viewDistance = player.clientViewDistance.coerceAtMost(Bukkit.getViewDistance()) + 1
                    val currentChunk = player.location.chunk
                    if (chunkId1.x > currentChunk.x + viewDistance || chunkId1.x < currentChunk.x - viewDistance || chunkId1.z > currentChunk.z + viewDistance || chunkId1.z < currentChunk.z - viewDistance) {
                        toRemove += chunkId1
                        chunks[player.world.name]?.get(chunkId1)?.remove(player.uniqueId)
                        runAsync {
                            AsyncPlayerChunkUnloadEvent(player,chunk).call()
                        }
                    }
                }
                worldChunks -= toRemove
            }
            worldChunks += chunkId
            AsyncPlayerChunkLoadEvent(player,chunk,packet).call()

        }
        event<PlayerQuitEvent> {
            val playerPair = playerToChunks.remove(it.player.uniqueId) ?: return@event
            for (chunkId in playerPair.second) {
                val chunkList = chunks[playerPair.first]?.get(chunkId) ?: continue
                chunkList -= it.player.uniqueId
                if (chunkList.isNotEmpty()) continue
                chunks[playerPair.first]?.remove(chunkId)
                val chunk = chunkId.toChunk(it.player.world)
                runAsync {
                    AsyncPlayerChunkUnloadEvent(it.player,chunk).call()
                }
            }
            playerToChunks.remove(it.player.uniqueId)
        }
        event<ChunkUnloadEvent> {
            chunks[it.chunk.world.name]?.remove(ChunkId(it.chunk.x, it.chunk.z))
        }
    }

    override fun disable(waves: Waves) {

    }

}