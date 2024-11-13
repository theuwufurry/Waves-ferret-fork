package gg.aquatic.waves.interactable.settings

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.aquaticseries.lib.util.mapPair
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.interactable.type.EntityInteractable
import gg.aquatic.waves.packetevents.EntityDataBuilder
import io.th0rgal.oraxen.api.OraxenItems
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic
import org.bukkit.Location

class OraxenEntityInteractableSettings(
    val furniture: FurnitureMechanic,
): InteractableSettings {

    override fun build(location: Location, audience: AquaticAudience): EntityInteractable {
        val item = OraxenItems.getItemById(furniture.itemID).build()
        val displaySettings = furniture.displayEntityProperties
        val fakeEntity = FakeEntity(EntityTypes.ITEM_DISPLAY, location, 50) {
            entityData += EntityDataBuilder.ITEM_DISPLAY
                .setItem(item)
                .setItemTransformation(displaySettings.displayTransform)
                .setScale(displaySettings.scale.x, displaySettings.scale.y, displaySettings.scale.z)
                .setBillboard(displaySettings.trackingRotation)
                .setWidth(displaySettings.displayWidth)
                .setHeight(displaySettings.displayHeight)
                .build()
                .mapPair { it.index to it }
        }

        val interactable = EntityInteractable(fakeEntity, audience)
        return interactable
    }

}