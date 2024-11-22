package gg.aquatic.waves.interactable.settings

import org.bukkit.configuration.ConfigurationSection

interface InteractableSettingsFactory {

    fun load(section: ConfigurationSection): InteractableSettings?

}