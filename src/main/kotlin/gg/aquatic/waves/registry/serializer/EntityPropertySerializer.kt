package gg.aquatic.waves.registry.serializer

import gg.aquatic.waves.interactable.settings.entityproperty.EntityProperty
import gg.aquatic.waves.registry.WavesRegistry
import org.bukkit.configuration.ConfigurationSection

object EntityPropertySerializer {

    fun fromSection(section: ConfigurationSection): EntityProperty? {
        val factory = WavesRegistry.ENTITY_PROPERTY_FACTORIES[section.getString("type")?.lowercase()] ?: return null
        return factory.serialize(section)
    }

    fun fromSections(sections: List<ConfigurationSection>): MutableList<EntityProperty> = sections.mapNotNull(
        ::fromSection
    ).toMutableList()

}