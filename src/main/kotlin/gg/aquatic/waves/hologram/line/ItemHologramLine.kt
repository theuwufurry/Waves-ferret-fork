package gg.aquatic.waves.hologram.line

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import gg.aquatic.waves.hologram.HologramLine
import gg.aquatic.waves.hologram.SpawnedHologramLine
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.Location
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class ItemHologramLine(
    val item: ItemStack,
    override val height: Double = 0.3,
    val scale: Float = 1.0f,
    val billboard: Billboard = Billboard.CENTER,
    val itemDisplayTransform: ItemDisplayTransform,
    override val filter: (Player) -> Boolean,
    override val failLine: HologramLine,
) : HologramLine() {
    override fun spawn(
        location: Location,
        player: Player,
        textUpdater: (Player, String) -> String
    ): SpawnedHologramLine {
        val id = SpigotReflectionUtil.generateEntityId()
        val spawned = SpawnedHologramLine(
            player,
            this,
            id,
            location,
            textUpdater
        )

        val spawnPacket = WrapperPlayServerSpawnEntity(
            id,
            UUID.randomUUID(),
            EntityTypes.ITEM_DISPLAY,
            SpigotConversionUtil.fromBukkitLocation(location),
            location.yaw,
            0,
            null
        )
        val entityData = EntityDataBuilder.ITEM_DISPLAY
            .setItem(item)
            .setItemTransformation(itemDisplayTransform)
            .setScale(Vector3f(scale, scale, scale))
            .setBillboard(billboard)
            .build()

        val metadataPacket = WrapperPlayServerEntityMetadata(id, entityData)

        val user = player.toUser()
        user.sendPacket(spawnPacket)
        user.sendPacket(metadataPacket)

        return spawned
    }

    override fun destroy(spawnedHologramLine: SpawnedHologramLine) {
        spawnedHologramLine.player.toUser().sendPacket(
            WrapperPlayServerDestroyEntities(spawnedHologramLine.entityId)
        )
    }

    override fun update(spawnedHologramLine: SpawnedHologramLine) {

    }

    override fun move(spawnedHologramLine: SpawnedHologramLine) {
        spawnedHologramLine.player.toUser().sendPacket(
            WrapperPlayServerEntityTeleport(
                spawnedHologramLine.entityId,
                SpigotConversionUtil.fromBukkitLocation(spawnedHologramLine.currentLocation),
                false
            )
        )
    }

}