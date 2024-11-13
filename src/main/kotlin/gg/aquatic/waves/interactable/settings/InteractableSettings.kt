package gg.aquatic.waves.interactable.settings

import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.waves.interactable.Interactable
import gg.aquatic.waves.interactable.InteractableInteractEvent
import org.bukkit.Location

interface InteractableSettings {
    fun build(location: Location, audience: AquaticAudience, onInteract: (InteractableInteractEvent) -> Unit): Interactable
}