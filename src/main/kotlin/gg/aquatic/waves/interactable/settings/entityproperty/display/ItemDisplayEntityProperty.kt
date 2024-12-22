package gg.aquatic.waves.interactable.settings.entityproperty.display

import gg.aquatic.waves.interactable.settings.entityproperty.EntityProperty
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.packetevents.type.ItemDisplayEntityDataBuilder
import gg.aquatic.waves.util.item.loadFromYml
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform

interface ItemDisplayEntityProperty : EntityProperty {

    class Item(val item: AquaticItem) : ItemDisplayEntityProperty {

        companion object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                val item = AquaticItem.loadFromYml(section)!!
                return Item(item)
            }

        }

        override fun apply(builder: EntityDataBuilder) {
            if (builder is ItemDisplayEntityDataBuilder) {
                builder.setItem(item.getItem())
            }
        }
    }

    class ItemTransformation(val transform: ItemDisplayTransform) : ItemDisplayEntityProperty {

        companion object : EntityProperty.Serializer {
            override fun serialize(section: ConfigurationSection): EntityProperty {
                val transformation = ItemDisplayTransform.valueOf(section.getString("item-transform", "NONE")!!)
                return ItemTransformation(transformation)
            }
        }

        override fun apply(builder: EntityDataBuilder) {
            if (builder is ItemDisplayEntityDataBuilder) {
                builder.setItemTransformation(transform)
            }
        }
    }

}