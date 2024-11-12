package gg.aquatic.waves.fake.block

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.type.EntityType
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import gg.aquatic.aquaticseries.lib.chunkcache.ChunkCacheHandler
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationCacheHandler
import gg.aquatic.waves.chunk.chunkId
import gg.aquatic.waves.chunk.trackedChunks
import gg.aquatic.waves.fake.FakeObject
import gg.aquatic.waves.fake.FakeObjectHandler
import gg.aquatic.waves.fake.FakeObjectChunkBundle
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

open class FakeEntity(
    val type: EntityType, override var location: Location,
    override val viewRange: Int,
    consumer: FakeEntity.() -> Unit = {}
) : FakeObject() {

    override fun destroy() {
        for (player in isViewing) {
            hide(player)
        }
        FakeObjectHandler.tickableObjects -= this
        unregister()
        destroyed = true
    }

    val entityId = SpigotReflectionUtil.generateEntityId()
    val entityUUID = UUID.randomUUID()
    val entityData = HashMap<Int, EntityData>()
    val equipment = HashMap<EquipmentSlot, ItemStack>()

    init {
        consumer(this)
        FakeObjectHandler.tickableObjects += this
        for (viewer in viewers) {
            if (viewer.trackedChunks().contains(location.chunk.chunkId())) {
                show(viewer)
            }
        }
    }

    fun register() {
        if (registered) return
        registered = true
        var bundle =
            ChunkCacheHandler.getObject(location.chunk, FakeObjectChunkBundle::class.java) as? FakeObjectChunkBundle
        if (bundle == null) {
            bundle = FakeObjectChunkBundle()
            ChunkCacheHandler.registerObject(bundle, location.chunk)
        }
        bundle.entities += this
    }

    fun unregister() {
        if (!registered) return
        registered = false
        val bundle =
            ChunkCacheHandler.getObject(location.chunk, FakeObjectChunkBundle::class.java) as? FakeObjectChunkBundle
                ?: return
        bundle.entities -= this
    }

    fun updateEntity(func: FakeEntity.() -> Unit) {
        func(this)

        for (player in isViewing) {
            sendUpdate(player)
        }
    }

    private fun sendUpdate(player: Player) {
        val user = player.toUser()
        if (entityData.isNotEmpty()) {
            val packet = WrapperPlayServerEntityMetadata(entityId, entityData.values.toMutableList())
            user.sendPacket(packet)
        }

        if (equipment.isNotEmpty()) {
            val packet = WrapperPlayServerEntityEquipment(
                entityId,
                equipment.map { Equipment(it.key, SpigotConversionUtil.fromBukkitItemStack(it.value)) })
            user.sendPacket(packet)
        }
    }

    override fun addViewer(player: Player) {
        if (viewers.contains(player)) return
        viewers.add(player)
        if (player.location.distanceSquared(location) <= viewRange * viewRange) {
            show(player)
        }
    }

    override fun removeViewer(uuid: UUID) {
        viewers.removeIf { it.uniqueId == uuid }
        isViewing.removeIf { it.uniqueId == uuid }
    }

    override fun removeViewer(player: Player) {
        if (isViewing.contains(player)) {
            hide(player)
        }
        FakeObjectHandler.handlePlayerRemove(player, this, true)
    }

    override fun show(player: Player) {
        isViewing.add(player)
        val spawnPacket = WrapperPlayServerSpawnEntity(
            entityId,
            entityUUID,
            type,
            SpigotConversionUtil.fromBukkitLocation(location),
            location.yaw,
            0,
            null
        )
        player.toUser().sendPacket(spawnPacket)
        sendUpdate(player)
    }

    override fun hide(player: Player) {
        isViewing.remove(player)
        val destroyPacket = WrapperPlayServerDestroyEntities(entityId)
        player.toUser().sendPacket(destroyPacket)
    }

    override fun tick() {

    }

    fun teleport(location: Location) {
        this.location = location
        if (registered) {
            unregister()
            register()
        }
        val packet = WrapperPlayServerEntityTeleport(
            entityId, SpigotConversionUtil.fromBukkitLocation(location), false
        )
        for (player in isViewing) {
            player.toUser().sendPacket(packet)
        }
    }
}