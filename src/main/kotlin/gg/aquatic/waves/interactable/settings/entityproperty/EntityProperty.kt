package gg.aquatic.waves.interactable.settings.entityproperty

import gg.aquatic.waves.packetevents.EntityDataBuilder
import org.bukkit.configuration.ConfigurationSection

interface EntityProperty {

    fun apply(builder: EntityDataBuilder)

    interface Serializer {

        fun serialize(section: ConfigurationSection): EntityProperty
    }
}