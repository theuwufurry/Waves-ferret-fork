package gg.aquatic.waves.chunk

import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData
import gg.aquatic.aquaticseries.lib.util.call
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.runAsync
import gg.aquatic.aquaticseries.lib.util.runSync
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.util.packetEvent
import gg.aquatic.waves.util.player
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.util.UUID
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
            val player = this.player()
            val chunkId = ChunkId(packet.column.x, packet.column.z)
            chunks.getOrPut(player.world.name) { hashMapOf() }.getOrPut(chunkId) { ConcurrentHashMap.newKeySet() }.add(player.uniqueId)
            var playerPair = playerToChunks.getOrPut(player.uniqueId) { player.world.name to ConcurrentHashMap.newKeySet() }
            if (playerPair.first != player.location.world!!.name) {
                for (chunkId1 in playerPair.second) {
                    val chunkList = chunks[playerPair.first]?.get(chunkId1) ?: continue
                    chunkList -= player.uniqueId
                    runSync {
                        PlayerChunkUnloadEvent(player,chunkId1.toChunk(player.world)).call()
                    }
                    if (chunkList.isEmpty()) {
                        chunks[playerPair.first]?.remove(chunkId1)
                    }
                }
                playerPair = player.world.name to ConcurrentHashMap.newKeySet()
                playerToChunks[player.uniqueId] = playerPair
            }
            val toRemove = mutableSetOf<ChunkId>()
            for (chunkId1 in playerPair.second) {
                val viewDistance = player.clientViewDistance.coerceAtMost(Bukkit.getViewDistance()) + 1
                val currentChunk = player.location.chunk
                if (chunkId1.x > currentChunk.x + viewDistance || chunkId1.x < currentChunk.x - viewDistance || chunkId1.z > currentChunk.z + viewDistance || chunkId1.z < currentChunk.z - viewDistance) {
                    toRemove += chunkId1
                    chunks[player.world.name]?.get(chunkId1)?.remove(player.uniqueId)
                    runSync {
                        PlayerChunkUnloadEvent(player,chunkId1.toChunk(player.world)).call()
                    }
                }
            }
            playerPair.second -= toRemove
            playerPair.second += chunkId
            AsyncPlayerChunkLoadEvent(player,chunkId.toChunk(player.world),packet).call()
        }
        event<PlayerQuitEvent> {
            val playerPair = playerToChunks.remove(it.player.uniqueId) ?: return@event
            runAsync {
                for (chunkId in playerPair.second) {
                    val chunkList = chunks[playerPair.first]?.get(chunkId) ?: continue
                    chunkList -= it.player.uniqueId
                    if (chunkList.isNotEmpty()) continue
                    chunks[playerPair.first]?.remove(chunkId)
                    runSync {
                        PlayerChunkUnloadEvent(it.player,chunkId.toChunk(it.player.world)).call()
                    }
                }
                playerToChunks.remove(it.player.uniqueId)
            }
        }
        event<ChunkUnloadEvent> {
            chunks[it.chunk.world.name]?.remove(ChunkId(it.chunk.x, it.chunk.z))
        }
    }

    override fun disable(waves: Waves) {

    }

}