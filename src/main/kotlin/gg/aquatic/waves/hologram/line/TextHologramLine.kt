package gg.aquatic.waves.hologram.line

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import gg.aquatic.waves.hologram.*
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import gg.aquatic.waves.util.collection.checkRequirements
import gg.aquatic.waves.util.getSectionList
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Player
import java.awt.Color
import java.util.*

class TextHologramLine(
    override val height: Double,
    override val filter: (Player) -> Boolean,
    override val failLine: HologramLine?,
    val text: String,
    val lineWidth: Int,
    val scale: Float = 1.0f,
    val billboard: Billboard = Billboard.CENTER,
    val hasShadow: Boolean = true,
    val defaultBackground: Boolean = true,
    val backgroundColor: Color? = null,
    val isSeeThrough: Boolean = true,
    val transformationDuration: Int = 0,
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

        createEntity(spawned)

        return spawned
    }

    override fun destroy(spawnedHologramLine: SpawnedHologramLine) {
        spawnedHologramLine.player.toUser()?.sendPacket(
            WrapperPlayServerDestroyEntities(spawnedHologramLine.entityId)
        )
    }

    override fun update(spawnedHologramLine: SpawnedHologramLine) {
        val text = spawnedHologramLine.textUpdater(spawnedHologramLine.player, this.text)
        val builder = EntityDataBuilder.TEXT_DISPLAY()
        builder.setText(text.toMMComponent())
        builder.setLineWidth(lineWidth)
        val entityData = builder
            .setScale(Vector3f(scale, scale, scale))
            .setBillboard(billboard)
            .build()

        val metadataPacket = WrapperPlayServerEntityMetadata(spawnedHologramLine.entityId, entityData)
        spawnedHologramLine.player.toUser()?.sendPacket(metadataPacket)
    }

    override fun move(spawnedHologramLine: SpawnedHologramLine) {
        spawnedHologramLine.player.toUser()?.sendPacket(
            WrapperPlayServerEntityTeleport(
                spawnedHologramLine.entityId,
                SpigotConversionUtil.fromBukkitLocation(spawnedHologramLine.currentLocation),
                false
            )
        )
    }

    override fun createEntity(spawnedHologramLine: SpawnedHologramLine) {
        val id = spawnedHologramLine.entityId
        val location = spawnedHologramLine.currentLocation
        val spawnPacket = WrapperPlayServerSpawnEntity(
            id,
            UUID.randomUUID(),
            EntityTypes.TEXT_DISPLAY,
            SpigotConversionUtil.fromBukkitLocation(location),
            location.yaw,
            0,
            null
        )
        val entityData = buildData(spawnedHologramLine)
        val metadataPacket = WrapperPlayServerEntityMetadata(id, entityData)

        val user = spawnedHologramLine.player.toUser() ?: return
        user.sendPacket(spawnPacket)
        user.sendPacket(metadataPacket)
    }

    override fun buildData(spawnedHologramLine: SpawnedHologramLine): List<EntityData> {
        val builder = EntityDataBuilder.TEXT_DISPLAY()
        builder.setInterpolationDelay(0)
        builder.setTransformationInterpolationDuration(transformationDuration)
        builder.setPosRotInterpolationDuration(transformationDuration)
        builder.setText(text.toMMComponent())
        builder.setLineWidth(lineWidth)
        builder.hasShadow(hasShadow)
        builder.useDefaultBackgroundColor(defaultBackground)
        builder.isSeeThrough(isSeeThrough)
        backgroundColor?.let {
            builder.setBackgroundColor(it.rgb)
        }
        val entityData = builder
            .setScale(Vector3f(scale, scale, scale))
            .setBillboard(billboard)
            .build()
        return entityData
    }

    class Settings(
        val height: Double,
        val text: String,
        val lineWidth: Int,
        val scale: Float = 1.0f,
        val billboard: Billboard = Billboard.CENTER,
        val conditions: List<ConfiguredRequirement<Player>>,
        val hasShadow: Boolean,
        val defaultBackground: Boolean,
        val backgroundColor: Color?,
        val isSeeThrough: Boolean,
        val transformationDuration: Int,
        val failLine: LineSettings?,
    ): LineSettings {
        override fun create(): HologramLine {
            return TextHologramLine(
                height,
                { p ->
                    conditions.checkRequirements(p)
                },
                failLine?.create(),
                text,
                lineWidth,
                scale,
                billboard,
                hasShadow,
                defaultBackground,
                backgroundColor,
                isSeeThrough,
                transformationDuration,
            )
        }
    }

    companion object: LineFactory {
        override fun load(section: ConfigurationSection): LineSettings? {
            val text = section.getString("text") ?: return null
            val height = section.getDouble("height", 0.5)
            val lineWidth = section.getInt("line-width", 100)
            val scale = section.getDouble("scale", 1.0).toFloat()
            val billboard = Billboard.valueOf(section.getString("billboard", "CENTER")!!.uppercase())
            val conditions = RequirementSerializer.fromSections<Player>(section.getSectionList("view-conditions"))
            val failLine = section.getConfigurationSection("fail-line")?.let {
                HologramSerializer.loadLine(it)
            }
            val hasShadow = section.getBoolean("has-shadow", false)
            val defaultBackground = section.getBoolean("default-background", true)
            val backgroundColorStr = section.getString("background-color")
            val isSeeThrough = section.getBoolean("is-see-through", true)
            val transformationDuration = section.getInt("transformation-duration", 0)
            val backgroundColor = if (backgroundColorStr != null) {
                val args = backgroundColorStr.split(";").map { it.toIntOrNull() ?: 0 }
                Color(args[0], args[1], args[2], args.getOrNull(3) ?: 255)
            } else null
            return Settings(
                height,
                text,
                lineWidth,
                scale,
                billboard,
                conditions,
                hasShadow,
                defaultBackground,
                backgroundColor,
                isSeeThrough,
                transformationDuration,
                failLine,
            )
        }
    }
}