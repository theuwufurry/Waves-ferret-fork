package gg.aquatic.waves.interactable.settings

import gg.aquatic.waves.interactable.Interactable
import org.bukkit.Location

interface InteractableSettings {
    fun build(location: Location): Interactable
}