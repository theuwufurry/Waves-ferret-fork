package gg.aquatic.waves.fake.entity

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.type.EntityType
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.wrapper.play.server.*
import gg.aquatic.waves.chunk.cache.ChunkCacheHandler
import gg.aquatic.waves.chunk.chunkId
import gg.aquatic.waves.chunk.trackedChunks
import gg.aquatic.waves.fake.EntityBased
import gg.aquatic.waves.fake.FakeObject
import gg.aquatic.waves.fake.FakeObjectChunkBundle
import gg.aquatic.waves.fake.FakeObjectHandler
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.collection.mapPair
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

open class FakeEntity(
    val type: EntityType, override var location: Location,
    override val viewRange: Int,
    audience: AquaticAudience,
    consumer: FakeEntity.() -> Unit = {},
    override var onInteract: (FakeEntityInteractEvent) -> Unit = {},
    var onUpdate: (Player) -> Unit = {},
) : FakeObject(), EntityBased {

    @Volatile
    override var audience: AquaticAudience = FilterAudience { false }
        set(value) {
            field = value
            for (viewer in viewers.toMutableList()) {
                if (field.canBeApplied(viewer) && viewer.isOnline) continue
                removeViewer(viewer)
            }
            for (player in
            location.world!!.players.filter { !viewers.contains(it) }) {
                if (!field.canBeApplied(player)) continue
                addViewer(player)
            }
        }

    override fun destroy() {
        destroyed = true
        for (player in isViewing.toSet()) {
            hide(player)
        }
        FakeObjectHandler.tickableObjects -= this
        unregister()
        FakeObjectHandler.idToEntity -= entityId
    }

    val atomicEntityId = AtomicInteger(SpigotReflectionUtil.generateEntityId())
    override val entityId: Int get() = atomicEntityId.get()
    val entityUUID = UUID.randomUUID()
    val entityData = ConcurrentHashMap<Int, EntityData>()
    val equipment = ConcurrentHashMap<EquipmentSlot, ItemStack>()
    val passengers = ConcurrentHashMap.newKeySet<Int>()

    init {
        if (type == EntityTypes.ITEM) {
            entityData += EntityDataBuilder.ITEM().setItem(ItemStack(Material.STONE)).build().mapPair { it.index to it }
        }
        consumer(this)
        this.audience = audience
        FakeObjectHandler.tickableObjects += this
        FakeObjectHandler.idToEntity += entityId to this
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
        val user = player.toUser() ?: return
        if (entityData.isNotEmpty()) {
            val packet = WrapperPlayServerEntityMetadata(entityId, entityData.values.toList())
            user.sendPacket(packet)
        }
        if (equipment.isNotEmpty()) {
            val packet = WrapperPlayServerEntityEquipment(
                entityId,
                equipment.map { Equipment(it.key, SpigotConversionUtil.fromBukkitItemStack(it.value)) })
            user.sendPacket(packet)
        }
        val passengersPacket = WrapperPlayServerSetPassengers(entityId, passengers.toIntArray())
        user.sendPacket(passengersPacket)

        onUpdate(player)
    }

    override fun addViewer(player: Player) {
        if (viewers.contains(player)) return
        viewers.add(player)
        if (player.world.name != location.world!!.name) return
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
        if (isViewing.contains(player)) return
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
        player.toUser()?.sendPacket(spawnPacket)
        sendUpdate(player)
    }

    override fun hide(player: Player) {
        isViewing.remove(player)
        val destroyPacket = WrapperPlayServerDestroyEntities(entityId)
        player.toUser()?.sendPacket(destroyPacket)
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
            player.toUser()?.sendPacket(packet)
        }
    }
}