package gg.aquatic.waves.interactable.settings.entityproperty

import gg.aquatic.waves.packetevents.EntityDataBuilder
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

interface EntityProperty {

    fun apply(builder: EntityDataBuilder, player: Player?, updater: (Player, String) -> String)

    interface Serializer {

        fun serialize(section: ConfigurationSection): EntityProperty
    }
}