package gg.aquatic.waves.fake

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.type.EntityType
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import gg.aquatic.aquaticseries.lib.chunkcache.location.LocationCacheHandler
import gg.aquatic.waves.util.blockLocation
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class FakeEntity(
    val type: EntityType, location: Location,
    override val viewRange: Int
) :
    FakeObject() {
    override val location: Location = location.blockLocation()

    override fun destroy() {
        for (player in isViewing) {
            show(player)
        }
        unregister()
    }

    val entityId = SpigotReflectionUtil.generateEntityId()
    val entityUUID = UUID.randomUUID()
    val entityData = HashMap<Int, EntityData>()
    val equipment = HashMap<EquipmentSlot, ItemStack>()

    init {
        FakeObjectHandler.tickableObjects += this
    }

    fun register() {
        if (registered) return
        registered = true
        var bundle =
            LocationCacheHandler.getObject(location, FakeObjectLocationBundle::class.java) as? FakeObjectLocationBundle
        if (bundle == null) {
            bundle = FakeObjectLocationBundle()
            LocationCacheHandler.registerObject(bundle, FakeObjectLocationBundle::class.java, location)
        }
        bundle.entities += this
    }

    fun unregister() {
        if (!registered) return
        registered = false
        val bundle =
            LocationCacheHandler.getObject(location, FakeObjectLocationBundle::class.java) as? FakeObjectLocationBundle
                ?: return
        bundle.entities -= this

        if (bundle.blocks.isEmpty() && bundle.entities.isEmpty()) {
            LocationCacheHandler.unregisterObject(FakeObjectLocationBundle::class.java, location)
        }
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
    }

    override fun removeViewer(player: Player) {
        hide(player)
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
        tickRange()
    }
}