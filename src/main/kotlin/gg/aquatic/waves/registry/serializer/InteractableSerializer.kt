package gg.aquatic.waves.registry.serializer

import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.registry.WavesRegistry
import org.bukkit.configuration.ConfigurationSection

object InteractableSerializer {

    fun load(section: ConfigurationSection): InteractableSettings? {
        val type = section.getString("type")?.uppercase() ?: return null
        val factory = WavesRegistry.INTERACTABLE_FACTORIES[type] ?: return null
        return factory.load(section)
    }

}