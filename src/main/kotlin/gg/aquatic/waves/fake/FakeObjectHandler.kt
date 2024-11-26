package gg.aquatic.waves.fake

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import gg.aquatic.aquaticseries.lib.chunkcache.ChunkCacheHandler
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.runAsync
import gg.aquatic.aquaticseries.lib.util.runAsyncTimer
import gg.aquatic.aquaticseries.lib.util.runLaterSync
import gg.aquatic.waves.Waves
import gg.aquatic.waves.chunk.AsyncPlayerChunkLoadEvent
import gg.aquatic.waves.chunk.PlayerChunkUnloadEvent
import gg.aquatic.waves.chunk.chunkId
import gg.aquatic.waves.fake.block.FakeBlock
import gg.aquatic.waves.fake.block.FakeBlockInteractEvent
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.FakeEntityInteractEvent
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.util.packetEvent
import gg.aquatic.waves.util.player
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.ConcurrentHashMap

object FakeObjectHandler : WaveModule {
    override val type: WaveModules = WaveModules.FAKE_OBJECTS

    internal val tickableObjects = ConcurrentHashMap.newKeySet<FakeObject>()
    internal val idToEntity = ConcurrentHashMap<Int, FakeEntity>()
    internal val locationToBlocks = ConcurrentHashMap<Location, MutableSet<FakeBlock>>()
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

        event<AsyncPlayerChunkLoadEvent> {
            val obj =
                ChunkCacheHandler.getObject(it.chunk, FakeObjectChunkBundle::class.java) as? FakeObjectChunkBundle
                    ?: return@event
            tickableObjects += obj.blocks
            tickableObjects += obj.entities
            for (block in obj.blocks) {
                if (block.viewers.contains(it.player)) {
                    val index = (block.location.y.toInt()+64) / 16
                    val chunk = it.wrappedPacket.column.chunks[index]
                    chunk.set(
                        block.location.x.toInt() and 0xf,
                        block.location.y.toInt() and 0xf,
                        block.location.z.toInt() and 0xf,
                        SpigotConversionUtil.fromBukkitBlockData(block.block.blockData)
                    )
                    block.isViewing += it.player
                }
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
        packetEvent<PacketReceiveEvent> {
            if (packetType == PacketType.Play.Client.PLAYER_DIGGING) {
                val packet = WrapperPlayClientPlayerDigging(this)
                Bukkit.broadcastMessage("Packet Interacted - RIGHT")

                val player = player()

                val blocks = locationToBlocks[player.world.getBlockAt(
                    packet.blockPosition.x,
                    packet.blockPosition.y,
                    packet.blockPosition.z
                ).location] ?: return@packetEvent

                for (block in blocks) {
                    if (block.viewers.contains(player)) {
                        val event = FakeBlockInteractEvent(
                            block,
                            player,
                            true
                        )
                        block.onInteract(event)
                        if (!block.destroyed) {
                            this.isCancelled = true
                        }
                        break
                    }
                }
            } else if (packetType == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
                val packet = WrapperPlayClientPlayerBlockPlacement(this)
                val isOffhand = (packet.hand == InteractionHand.OFF_HAND)
                Bukkit.broadcastMessage("Packet Interacted - RIGHT")

                val player = player()

                val blocks = locationToBlocks[player.world.getBlockAt(
                    packet.blockPosition.x,
                    packet.blockPosition.y,
                    packet.blockPosition.z
                ).location] ?: return@packetEvent

                for (block in blocks) {
                    if (block.viewers.contains(player)) {
                        if (isOffhand) {
                            this.isCancelled = true
                            break
                        }
                        val event = FakeBlockInteractEvent(
                            block,
                            player,
                            false
                        )
                        block.onInteract(event)
                        if (!block.destroyed) {
                            this.isCancelled = true
                        }
                        break
                    }
                }
            }
        }
        event<PlayerInteractEvent> {
            val blocks = locationToBlocks[it.clickedBlock?.location ?: return@event] ?: return@event
            for (block in blocks) {
                if (block.viewers.contains(it.player)) {
                    it.isCancelled = true
                    val event = FakeBlockInteractEvent(
                        block,
                        it.player,
                        it.action == Action.LEFT_CLICK_BLOCK || it.action == Action.LEFT_CLICK_AIR
                    )
                    block.onInteract(event)
                    if (!block.destroyed) {
                        if (it.action == Action.RIGHT_CLICK_AIR || it.action == Action.RIGHT_CLICK_BLOCK) {
                            runLaterSync(1) {
                                block.show(it.player)
                            }
                        }
                        block.show(it.player)
                    }
                    break
                }
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

        /*
        if (fakeObject.location.chunk.trackedByPlayers().isEmpty() && fakeObject.isViewing.isEmpty()) {
            objectRemovalQueue += fakeObject
        }
         */
    }

    override fun disable(waves: Waves) {

    }
}