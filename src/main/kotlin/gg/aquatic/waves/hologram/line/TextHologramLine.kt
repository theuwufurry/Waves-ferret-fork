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
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.Location
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Player
import java.util.*

class TextHologramLine(
    override val height: Double,
    override val filter: (Player) -> Boolean,
    override val failLine: HologramLine?,
    val text: String,
    val lineWidth: Int,
    val scale: Float = 1.0f,
    val billboard: Billboard = Billboard.CENTER,
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
            EntityTypes.TEXT_DISPLAY,
            SpigotConversionUtil.fromBukkitLocation(location),
            location.yaw,
            0,
            null
        )
        val builder = EntityDataBuilder.TEXT_DISPLAY
        builder.setText(text.toMMComponent())
        builder.setLineWidth(lineWidth)
        val entityData = builder
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
        val text = spawnedHologramLine.textUpdater(spawnedHologramLine.player, this.text)
        val builder = EntityDataBuilder.TEXT_DISPLAY
        builder.setText(text.toMMComponent())
        builder.setLineWidth(lineWidth)
        val entityData = builder
            .setScale(Vector3f(scale, scale, scale))
            .setBillboard(billboard)
            .build()

        val metadataPacket = WrapperPlayServerEntityMetadata(spawnedHologramLine.entityId, entityData)
        spawnedHologramLine.player.toUser().sendPacket(metadataPacket)
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