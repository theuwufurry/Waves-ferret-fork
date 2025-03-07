package gg.aquatic.waves.interactable.settings

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.interactable.Interactable
import gg.aquatic.waves.interactable.InteractableInteractEvent
import gg.aquatic.waves.interactable.type.EntityInteractable
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.collection.mapPair
import gg.aquatic.waves.util.item.loadFromYml
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.util.Vector

class ItemDisplayInteractableSettings(
    val offset: Vector,
    val item: AquaticItem,
    val itemTransform: ItemDisplayTransform,
    val scale: Vector,
    val billboard: Billboard
): InteractableSettings {
    override fun build(
        location: Location,
        audience: AquaticAudience,
        onInteract: (InteractableInteractEvent) -> Unit
    ): Interactable {
        val fakeEntity = FakeEntity(EntityTypes.ITEM_DISPLAY, location.clone().add(offset), 50, audience, consumer =  {
            entityData += EntityDataBuilder.ITEM_DISPLAY()
                .setItem(item.getItem())
                .setItemTransformation(itemTransform)
                .setScale(scale.x, scale.y, scale.z)
                .setBillboard(billboard)
                .build()
                .mapPair { it.index to it }
        })

        val interactable = EntityInteractable(fakeEntity, onInteract)
        return interactable
    }

    companion object: InteractableSettingsFactory {
        override fun load(section: ConfigurationSection): InteractableSettings? {
            val offsetStr = section.getString("offset","0;0;0")!!.split(";")
            val offset = Vector(
                offsetStr[0].toDouble(),
                offsetStr[1].toDouble(),
                offsetStr[2].toDouble()
            )
            val item = AquaticItem.loadFromYml(section.getConfigurationSection("item")) ?: return null
            val itemTransform = ItemDisplayTransform.valueOf(section.getString("item-transform") ?: "NONE")
            val scaleStr = section.getString("scale","1;1;1")!!.split(";")
            val scale = Vector(
                scaleStr[0].toDouble(),
                scaleStr[1].toDouble(),
                scaleStr[2].toDouble()
            )
            val billboard = Billboard.valueOf(section.getString("billboard") ?: "FIXED")
            return ItemDisplayInteractableSettings(offset, item, itemTransform, scale, billboard)
        }

    }
}