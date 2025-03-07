package gg.aquatic.waves.interactable.settings.entityproperty

import gg.aquatic.waves.packetevents.EntityDataBuilder
import org.bukkit.configuration.ConfigurationSection

interface EntityProperty {

    fun apply(builder: EntityDataBuilder, updater: (String) -> String)

    interface Serializer {

        fun serialize(section: ConfigurationSection): EntityProperty
    }
}