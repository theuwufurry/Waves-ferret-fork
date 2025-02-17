package gg.aquatic.waves.interactable.settings

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.interactable.Interactable
import gg.aquatic.waves.interactable.InteractableInteractEvent
import gg.aquatic.waves.interactable.settings.entityproperty.EntityProperty
import gg.aquatic.waves.interactable.type.EntityInteractable
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.registry.serializer.EntityPropertySerializer
import gg.aquatic.waves.util.collection.mapPair
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector

class EntityInteractableSettings(
    val props: HashSet<EntityProperty>,
    val offset: Vector,
    val yawPitch: Pair<Float, Float>
) : InteractableSettings {
    override fun build(
        location: Location,
        audience: AquaticAudience,
        onInteract: (InteractableInteractEvent) -> Unit
    ): Interactable {
        val fakeEntity = FakeEntity(EntityTypes.ITEM_DISPLAY, location.clone().add(offset).apply {
            yaw = yawPitch.first
            pitch = yawPitch.second
        }, 50, audience, consumer = {
            val builder = EntityDataBuilder.ANY
            for (prop in props) {
                prop.apply(builder) { str -> str }
            }
            entityData += builder.build().mapPair { it.index to it }
        })
        fakeEntity.register()

        val interactable = EntityInteractable(fakeEntity, onInteract)
        return interactable
    }

    companion object : InteractableSettingsFactory {
        override fun load(section: ConfigurationSection): InteractableSettings {
            val props = section.getConfigurationSection("properties")
                ?.let { EntityPropertySerializer.fromSection(it).toHashSet() } ?: hashSetOf()
            val offsetStrs = section.getString("offset", "0;0;0")!!.split(";")
            val offset = Vector(
                offsetStrs.getOrElse(0) { "0" }.toDouble(),
                offsetStrs.getOrElse(1) { "0" }.toDouble(),
                offsetStrs.getOrElse(2) { "0" }.toDouble()
            )
            val yawPitch = (
                    offsetStrs.getOrElse(3) {"0"}.toFloat()
                    ) to (
                    offsetStrs.getOrElse(4) {"0"}.toFloat())
            return EntityInteractableSettings(props, offset, yawPitch)
        }

    }
}